package com.playzone.pems.application.contrato.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.notificacion.dto.command.CrearNotificacionCommand;
import com.playzone.pems.application.notificacion.port.out.CrearNotificacionPort;
import com.playzone.pems.application.contrato.dto.command.ActualizarContratoCommand;
import com.playzone.pems.application.contrato.dto.command.CambiarEstadoContratoCommand;
import com.playzone.pems.application.contrato.dto.command.GenerarContratoCommand;
import com.playzone.pems.application.contrato.dto.command.SubirDocumentoCommand;
import com.playzone.pems.application.contrato.dto.query.ActividadContratoQuery;
import com.playzone.pems.application.contrato.dto.query.ContratoPageQuery;
import com.playzone.pems.application.contrato.dto.query.ContratoQuery;
import com.playzone.pems.application.contrato.dto.query.DocumentoContratoQuery;
import com.playzone.pems.application.contrato.port.in.ActualizarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.CambiarEstadoContratoUseCase;
import com.playzone.pems.application.contrato.port.in.FirmarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.GenerarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.ListarContratosUseCase;
import com.playzone.pems.application.contrato.port.in.ObtenerContratoUseCase;
import com.playzone.pems.application.contrato.port.in.ReemplazarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.SubirDocumentoContratoUseCase;
import com.playzone.pems.domain.contrato.exception.ContratoNotFoundException;
import com.playzone.pems.domain.contrato.model.ActividadContrato;
import com.playzone.pems.domain.contrato.model.Contrato;
import com.playzone.pems.domain.contrato.model.DocumentoContrato;
import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import com.playzone.pems.domain.contrato.repository.ActividadContratoRepository;
import com.playzone.pems.domain.contrato.repository.ContratoRepository;
import com.playzone.pems.domain.contrato.repository.DocumentoContratoRepository;
import com.playzone.pems.domain.storage.StoragePort;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContratoService
        implements GenerarContratoUseCase,
                   FirmarContratoUseCase,
                   ObtenerContratoUseCase,
                   ActualizarContratoUseCase,
                   ListarContratosUseCase,
                   CambiarEstadoContratoUseCase,
                   SubirDocumentoContratoUseCase,
                   ReemplazarContratoUseCase {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ContratoRepository          contratoRepository;
    private final DocumentoContratoRepository documentoRepository;
    private final ActividadContratoRepository actividadRepository;
    private final StoragePort                 storagePort;
    private final String                      bucketPrivado;
    private final RegistrarLogUseCase         auditoria;
    private final CrearNotificacionPort       crearNotificacionPort;

    public ContratoService(ContratoRepository contratoRepository,
                           DocumentoContratoRepository documentoRepository,
                           ActividadContratoRepository actividadRepository,
                           StoragePort storagePort,
                           @Value("${supabase.storage.bucket-privado}") String bucketPrivado,
                           RegistrarLogUseCase auditoria,
                           CrearNotificacionPort crearNotificacionPort) {
        this.contratoRepository    = contratoRepository;
        this.documentoRepository   = documentoRepository;
        this.actividadRepository   = actividadRepository;
        this.storagePort           = storagePort;
        this.bucketPrivado         = bucketPrivado;
        this.auditoria             = auditoria;
        this.crearNotificacionPort = crearNotificacionPort;
    }

    @Override
    @Transactional
    public ContratoQuery ejecutar(GenerarContratoCommand command) {
        if (contratoRepository.existsByEventoPrivado(command.getIdEventoPrivado())) {
            throw new ValidationException("Ya existe un contrato para ese evento privado.");
        }

        Contrato contrato = Contrato.builder()
                .idEventoPrivado(command.getIdEventoPrivado())
                .idUsuarioRedactor(command.getIdUsuarioRedactor())
                .estado(EstadoContrato.BORRADOR)
                .contenidoTexto(command.getContenidoTexto())
                .plantilla(command.getPlantilla())
                .version(1)
                .build();

        Contrato guardado = contratoRepository.save(contrato);

        String descripcionCreacion = command.getPlantilla() != null
                ? "Contrato generado desde plantilla: " + command.getPlantilla()
                : "Contrato generado.";
        registrarActividad(guardado.getId(), "CREADO", descripcionCreacion,
                command.getIdUsuarioRedactor());

        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                command.getIdUsuarioRedactor(), AuditoriaConstants.ACCION_CREAR, AuditoriaConstants.MOD_CONTRATOS,
                "Contrato", guardado.getId(),
                null, null,
                "Contrato #" + guardado.getId() + " creado para evento #" + command.getIdEventoPrivado(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));

        return toQuery(guardado);
    }

    @Override
    @Transactional
    public ContratoQuery ejecutar(Long idContrato, UUID idUsuario) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new ContratoNotFoundException(idContrato));

        if (!contrato.esEditable()) {
            throw new ValidationException("El contrato ya fue firmado y no puede modificarse.");
        }

        byte[] pdf = contrato.getContenidoTexto().getBytes(StandardCharsets.UTF_8);
        String key = "contratos/contrato_" + idContrato + "_" + LocalDateTime.now().format(FMT) + ".pdf";
        String pdfUrl = storagePort.upload(bucketPrivado, key, pdf, "application/pdf");

        Contrato firmado = contratoRepository.save(contrato.toBuilder()
                .estado(EstadoContrato.FIRMADO)
                .archivoPdfUrl(pdfUrl)
                .fechaFirma(FechaUtil.hoyPeru())
                .build());

        registrarActividad(idContrato, "FIRMADO", "PDF generado y contrato firmado.", idUsuario);

        crearNotificacionPort.notificar(CrearNotificacionCommand.builder()
                .tipoCodigo("CONTRATO_FIRMADO")
                .destinatarioUsuarioId(idUsuario)
                .entidadTipo("contrato")
                .entidadId(idContrato)
                .datosExtra(Map.of("contrato", String.valueOf(idContrato)))
                .build());

        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                idUsuario, AuditoriaConstants.ACCION_FIRMAR, AuditoriaConstants.MOD_CONTRATOS,
                "Contrato", idContrato,
                EstadoContrato.BORRADOR.getCodigo(), EstadoContrato.FIRMADO.getCodigo(),
                "Contrato #" + idContrato + " firmado y PDF generado",
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));

        return toQuery(firmado);
    }

    @Override
    @Transactional
    public ContratoQuery porId(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new ContratoNotFoundException(id));
        contrato = aplicarVencimientoSiCorresponde(contrato);
        return toQueryConDetalle(contrato);
    }

    @Override
    @Transactional
    public ContratoQuery porEvento(Long idEvento) {
        Contrato contrato = contratoRepository.findByEventoPrivado(idEvento)
                .orElseThrow(() -> new ContratoNotFoundException(
                        "No existe contrato para el evento " + idEvento));
        contrato = aplicarVencimientoSiCorresponde(contrato);
        return toQueryConDetalle(contrato);
    }

    @Override
    @Transactional
    public ContratoQuery reemplazar(Long idContratoActual, UUID idUsuarioAdmin) {
        Contrato actual = contratoRepository.findById(idContratoActual)
                .orElseThrow(() -> new ContratoNotFoundException(idContratoActual));

        contratoRepository.save(actual.toBuilder().estado(EstadoContrato.ARCHIVADO).build());
        registrarActividad(idContratoActual, "ARCHIVADO",
                "Contrato archivado al ser reemplazado.", idUsuarioAdmin);

        Contrato nuevo = contratoRepository.save(Contrato.builder()
                .idEventoPrivado(actual.getIdEventoPrivado())
                .idUsuarioRedactor(idUsuarioAdmin)
                .estado(EstadoContrato.BORRADOR)
                .contenidoTexto("")
                .version(1)
                .build());
        registrarActividad(nuevo.getId(), "CREADO",
                "Contrato creado en reemplazo del contrato #" + idContratoActual, idUsuarioAdmin);

        return toQueryConDetalle(nuevo);
    }

    @Override
    @Transactional
    public ContratoQuery ejecutar(ActualizarContratoCommand command) {
        Contrato contrato = contratoRepository.findById(command.getId())
                .orElseThrow(() -> new ContratoNotFoundException(command.getId()));

        if (!contrato.esEditable()) {
            throw new ValidationException("El contrato no es editable en su estado actual.");
        }

        Contrato actualizado = contratoRepository.save(contrato.toBuilder()
                .contenidoTexto(command.getContenidoTexto())
                .plantilla(command.getPlantilla())
                .observaciones(command.getObservaciones())
                .version(contrato.getVersion() + 1)
                .build());

        registrarActividad(command.getId(), "ACTUALIZADO",
                "Version incrementada a " + actualizado.getVersion(), command.getIdUsuario());

        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                command.getIdUsuario(), AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_CONTRATOS,
                "Contrato", command.getId(),
                null, "v" + actualizado.getVersion(),
                "Contrato #" + command.getId() + " actualizado a versión " + actualizado.getVersion(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));

        return toQuery(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public ContratoPageQuery ejecutar(
            String search, String estado, Long idSede, LocalDate fechaEvento, Pageable pageable) {

        Page<Contrato> pagina = contratoRepository.buscarConFiltros(
                search, estado, idSede, fechaEvento, pageable);

        return ContratoPageQuery.builder()
                .content(pagina.getContent().stream().map(this::toQuery).toList())
                .page(pagina.getNumber())
                .size(pagina.getSize())
                .totalElements(pagina.getTotalElements())
                .totalPages(pagina.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public ContratoQuery ejecutar(CambiarEstadoContratoCommand command) {
        Contrato contrato = contratoRepository.findById(command.getIdContrato())
                .orElseThrow(() -> new ContratoNotFoundException(command.getIdContrato()));

        if (contrato.getEstado().esTerminal()) {
            throw new ValidationException(
                    "El contrato ya se encuentra en estado terminal y no puede cambiarse.");
        }

        EstadoContrato nuevoEstado;
        try {
            nuevoEstado = EstadoContrato.valueOf(command.getNuevoEstado());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Estado no valido: " + command.getNuevoEstado());
        }

        Contrato actualizado = contratoRepository.save(
                contrato.toBuilder().estado(nuevoEstado).build());

        registrarActividad(command.getIdContrato(), command.getNuevoEstado(),
                command.getMotivo(), command.getIdUsuarioAdmin());

        String accionAudit = "CANCELADO".equals(command.getNuevoEstado())
                ? AuditoriaConstants.ACCION_CANCELAR
                : AuditoriaConstants.ACCION_ACTUALIZAR;
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                command.getIdUsuarioAdmin(), accionAudit, AuditoriaConstants.MOD_CONTRATOS,
                "Contrato", command.getIdContrato(),
                contrato.getEstado().getCodigo(), command.getNuevoEstado(),
                "Cambio de estado: " + contrato.getEstado().getCodigo() + " → " + command.getNuevoEstado(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));

        return toQuery(actualizado);
    }

    @Override
    @Transactional
    public DocumentoContratoQuery ejecutar(SubirDocumentoCommand command) {
        if (!contratoRepository.existsById(command.getIdContrato())) {
            throw new ContratoNotFoundException(command.getIdContrato());
        }

        DocumentoContrato documento = documentoRepository.save(
                DocumentoContrato.builder()
                        .idContrato(command.getIdContrato())
                        .nombre(command.getNombre())
                        .archivoUrl(command.getArchivoUrl())
                        .tipoArchivo(command.getTipoArchivo())
                        .tamanobytes(command.getTamanobytes())
                        .idUsuarioCarga(command.getIdUsuarioCarga())
                        .build());

        registrarActividad(command.getIdContrato(), "DOCUMENTO_SUBIDO",
                "Archivo: " + command.getNombre(), command.getIdUsuarioCarga());

        return toDocumentoQuery(documento);
    }

    @Override
    @Transactional
    public void eliminar(Long idDocumento) {
        documentoRepository.deleteById(idDocumento);
    }

    private void registrarActividad(
            Long idContrato, String accion, String descripcion, UUID idUsuario) {
        actividadRepository.save(ActividadContrato.builder()
                .idContrato(idContrato)
                .accion(accion)
                .descripcion(descripcion)
                .idUsuario(idUsuario)
                .build());
    }

    private Contrato aplicarVencimientoSiCorresponde(Contrato c) {
        if (!c.getEstado().esTerminal()
                && c.getEstado() != EstadoContrato.VENCIDO
                && c.getFechaEvento() != null
                && FechaUtil.esPasado(c.getFechaEvento())) {
            c = contratoRepository.save(c.toBuilder().estado(EstadoContrato.VENCIDO).build());
            registrarActividad(c.getId(), "VENCIDO",
                    "Contrato vencido por haber superado la fecha del evento.", null);
        }
        return c;
    }

    private ContratoQuery toQuery(Contrato c) {
        return ContratoQuery.builder()
                .id(c.getId())
                .idEventoPrivado(c.getIdEventoPrivado())
                .estado(c.getEstado().getCodigo())
                .esEditable(c.getEstado().esEditable())
                .contenidoTexto(c.getContenidoTexto())
                .archivoPdfUrl(c.getArchivoPdfUrl())
                .fechaFirma(c.getFechaFirma())
                .usuarioRedactor(c.getUsuarioRedactor())
                .plantilla(c.getPlantilla())
                .observaciones(c.getObservaciones())
                .version(c.getVersion())
                .nombreCliente(c.getNombreCliente())
                .correoCliente(c.getCorreoCliente())
                .tipoEvento(c.getTipoEvento())
                .fechaEvento(c.getFechaEvento())
                .turno(c.getTurno())
                .aforoDeclarado(c.getAforoDeclarado())
                .precioTotalContrato(c.getPrecioTotalContrato())
                .montoAdelanto(c.getMontoAdelanto())
                .saldoPendiente(c.getSaldoPendiente())
                .fechaCreacion(c.getFechaCreacion())
                .fechaActualizacion(c.getFechaActualizacion())
                .build();
    }

    private ContratoQuery toQueryConDetalle(Contrato c) {
        List<DocumentoContratoQuery> docs = documentoRepository
                .findByContrato(c.getId())
                .stream()
                .map(this::toDocumentoQuery)
                .toList();

        List<ActividadContratoQuery> actividades = actividadRepository
                .findByContrato(c.getId())
                .stream()
                .map(this::toActividadQuery)
                .toList();

        return toQuery(c).toBuilder()
                .documentos(docs)
                .actividades(actividades)
                .build();
    }

    private DocumentoContratoQuery toDocumentoQuery(DocumentoContrato d) {
        return DocumentoContratoQuery.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .archivoUrl(d.getArchivoUrl())
                .tipoArchivo(d.getTipoArchivo())
                .tamanobytes(d.getTamanobytes())
                .usuarioCarga(d.getNombreUsuarioCarga())
                .fechaCarga(d.getFechaCarga())
                .build();
    }

    private ActividadContratoQuery toActividadQuery(ActividadContrato a) {
        return ActividadContratoQuery.builder()
                .id(a.getId())
                .accion(a.getAccion())
                .descripcion(a.getDescripcion())
                .usuario(a.getNombreUsuario())
                .fechaAccion(a.getFechaAccion())
                .build();
    }
}
