package com.playzone.pems.application.facturacion.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.facturacion.dto.command.AnularComprobanteCommand;
import com.playzone.pems.application.facturacion.dto.command.EmitirComprobanteCommand;
import com.playzone.pems.application.facturacion.dto.query.ComprobanteQuery;
import com.playzone.pems.application.facturacion.port.in.AnularComprobanteUseCase;
import com.playzone.pems.application.facturacion.port.in.EmitirComprobanteUseCase;
import com.playzone.pems.application.facturacion.port.out.EnviarComprobanteSunatPort;
import com.playzone.pems.domain.facturacion.exception.ComprobanteNotFoundException;
import com.playzone.pems.domain.facturacion.exception.SunatRechazadoException;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.domain.facturacion.model.Comprobante;
import com.playzone.pems.domain.facturacion.model.SerieComprobante;
import com.playzone.pems.domain.facturacion.model.enums.EstadoComprobante;
import com.playzone.pems.domain.facturacion.repository.ComprobanteRepository;
import com.playzone.pems.domain.facturacion.repository.SerieComprobanteRepository;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class FacturacionService implements EmitirComprobanteUseCase, AnularComprobanteUseCase {

    private static final BigDecimal IGV = BigDecimal.valueOf(0.18);

    private final ComprobanteRepository     comprobanteRepository;
    private final SerieComprobanteRepository serieRepository;
    private final EnviarComprobanteSunatPort sunatPort;
    private final SupabaseAuthFacade         authFacade;
    private final RegistrarLogUseCase        auditoria;

    @Override
    @Transactional
    public ComprobanteQuery ejecutar(EmitirComprobanteCommand command) {
        SerieComprobante serie = serieRepository
                .findActivaBySedeAndTipo(command.getIdSede(), command.getTipoComprobante())
                .orElseThrow(() -> new ValidationException(
                        "No existe una serie activa para ese tipo de comprobante en la sede."));

        int correlativo    = serieRepository.incrementarCorrelativoYRetornar(command.getIdSede(), command.getTipoComprobante());
        String numeroCompleto = serie.getSerie() + "-" + String.format("%08d", correlativo);

        if (comprobanteRepository.existsByNumeroCompleto(numeroCompleto)) {
            throw new ValidationException("El número de comprobante ya existe: " + numeroCompleto);
        }

        Comprobante borrador = Comprobante.builder()
                .idPago(command.getIdPago())
                .tipoComprobante(command.getTipoComprobante())
                .estadoComprobante(EstadoComprobante.PENDIENTE)
                .idSerie(serie.getId())
                .serie(serie.getSerie())
                .correlativo(String.valueOf(correlativo))
                .numeroCompleto(numeroCompleto)
                .tipoDocReceptor(command.getTipoDocReceptor())
                .nroDocReceptor(command.getNroDocReceptor())
                .razonSocialReceptor(command.getRazonSocialReceptor())
                .direccionReceptor(command.getDireccionReceptor())
                .montoBase(BigDecimal.ZERO)
                .montoIgv(BigDecimal.ZERO)
                .montoTotal(BigDecimal.ZERO)
                .build();

        Comprobante guardado = comprobanteRepository.save(borrador);

        EnviarComprobanteSunatPort.RespuestaSunat respuesta =
                sunatPort.enviar(guardado.getId(), command);

        EstadoComprobante estado = respuesta.aceptado()
                ? EstadoComprobante.EMITIDO : EstadoComprobante.RECHAZADO;

        Comprobante actualizado = guardado.toBuilder()
                .estadoComprobante(estado)
                .hashSunat(respuesta.hashSunat())
                .cdrEstado(respuesta.cdrEstado())
                .cdrDescripcion(respuesta.cdrDescripcion())
                .xmlUrl(respuesta.xmlUrl())
                .pdfUrl(respuesta.pdfUrl())
                .build();

        Comprobante final_ = comprobanteRepository.save(actualizado);

        String nivelAudit = respuesta.aceptado() ? AuditoriaConstants.NIVEL_INFO : AuditoriaConstants.NIVEL_ERROR;
        String resultadoAudit = respuesta.aceptado() ? AuditoriaConstants.RESULTADO_EXITOSO : AuditoriaConstants.RESULTADO_FALLIDO;
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_EMITIR, AuditoriaConstants.MOD_FACTURACION,
                "Comprobante", guardado.getId(),
                null, numeroCompleto,
                "Comprobante " + numeroCompleto + " | estado=" + estado,
                null, null, nivelAudit, resultadoAudit));

        if (!respuesta.aceptado()) {
            throw new SunatRechazadoException(
                    numeroCompleto, respuesta.cdrEstado(), respuesta.cdrDescripcion());
        }

        return toQuery(final_);
    }

    @Override
    @Transactional
    public ComprobanteQuery ejecutar(AnularComprobanteCommand command) {
        Comprobante comprobante = comprobanteRepository.findById(command.getIdComprobante())
                .orElseThrow(() -> new ComprobanteNotFoundException(command.getIdComprobante()));

        if (!comprobante.esAnulable()) {
            throw new ValidationException("Solo se pueden anular comprobantes en estado EMITIDO.");
        }

        EnviarComprobanteSunatPort.RespuestaSunat respuesta =
                sunatPort.anular(command.getIdComprobante(), command.getMotivoAnulacion());

        Comprobante anulado = comprobante.toBuilder()
                .estadoComprobante(EstadoComprobante.ANULADO)
                .motivoAnulacion(command.getMotivoAnulacion())
                .cdrEstado(respuesta.cdrEstado())
                .cdrDescripcion(respuesta.cdrDescripcion())
                .build();

        Comprobante anuladoGuardado = comprobanteRepository.save(anulado);

        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                command.getIdUsuario(), AuditoriaConstants.ACCION_ANULAR, AuditoriaConstants.MOD_FACTURACION,
                "Comprobante", command.getIdComprobante(),
                comprobante.getNumeroCompleto(), null,
                "Comprobante " + comprobante.getNumeroCompleto() + " anulado: " + command.getMotivoAnulacion(),
                null, null, AuditoriaConstants.NIVEL_WARNING, AuditoriaConstants.RESULTADO_EXITOSO));

        return toQuery(anuladoGuardado);
    }

    private ComprobanteQuery toQuery(Comprobante c) {
        return ComprobanteQuery.builder()
                .id(c.getId())
                .numeroCompleto(c.getNumeroCompleto())
                .tipoComprobante(c.getTipoComprobante().getCodigo())
                .estadoComprobante(c.getEstadoComprobante().getCodigo())
                .rucEmisor(c.getRucEmisor())
                .razonSocialEmisor(c.getRazonSocialEmisor())
                .tipoDocReceptor(c.getTipoDocReceptor().getCodigo())
                .nroDocReceptor(c.getNroDocReceptor())
                .razonSocialReceptor(c.getRazonSocialReceptor())
                .montoBase(c.getMontoBase())
                .montoIgv(c.getMontoIgv())
                .montoTotal(c.getMontoTotal())
                .pdfUrl(c.getPdfUrl())
                .cdrEstado(c.getCdrEstado())
                .cdrDescripcion(c.getCdrDescripcion())
                .fechaEmision(c.getFechaEmision())
                .build();
    }
}