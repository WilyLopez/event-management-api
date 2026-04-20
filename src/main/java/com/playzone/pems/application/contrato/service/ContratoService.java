package com.playzone.pems.application.contrato.service;

import com.playzone.pems.application.contrato.dto.command.GenerarContratoCommand;
import com.playzone.pems.application.contrato.dto.query.ContratoQuery;
import com.playzone.pems.application.contrato.port.in.FirmarContratoUseCase;
import com.playzone.pems.application.contrato.port.in.GenerarContratoUseCase;
import com.playzone.pems.application.contrato.port.out.GenerarPdfContratoPort;
import com.playzone.pems.domain.contrato.exception.ContratoNotFoundException;
import com.playzone.pems.domain.contrato.model.Contrato;
import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import com.playzone.pems.domain.contrato.repository.ContratoRepository;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContratoService implements GenerarContratoUseCase, FirmarContratoUseCase {

    private final ContratoRepository    contratoRepository;
    private final GenerarPdfContratoPort pdfPort;

    @Override
    @Transactional
    public ContratoQuery ejecutar(GenerarContratoCommand command) {
        if (contratoRepository.existsByEventoPrivado(command.getIdEventoPrivado())) {
            throw new ValidationException("Ya existe un contrato para ese evento privado.");
        }

        Contrato contrato = Contrato.builder()
                .idEventoPrivado(command.getIdEventoPrivado())
                .estado(EstadoContrato.BORRADOR)
                .contenidoTexto(command.getContenidoTexto())
                .idUsuarioRedactor(command.getIdUsuarioRedactor())
                .build();

        return toQuery(contratoRepository.save(contrato));
    }

    @Override
    @Transactional
    public ContratoQuery ejecutar(Long idContrato) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new ContratoNotFoundException(idContrato));

        if (!contrato.esEditable()) {
            throw new ValidationException("El contrato ya fue firmado y no puede modificarse.");
        }

        String pdfUrl = pdfPort.generarYAlmacenar(idContrato, contrato.getContenidoTexto());

        Contrato firmado = contrato.toBuilder()
                .estado(EstadoContrato.FIRMADO)
                .archivoPdfUrl(pdfUrl)
                .fechaFirma(FechaUtil.hoyPeru())
                .build();

        return toQuery(contratoRepository.save(firmado));
    }

    private ContratoQuery toQuery(Contrato c) {
        return ContratoQuery.builder()
                .id(c.getId())
                .idEventoPrivado(c.getIdEventoPrivado())
                .estado(c.getEstado().getCodigo())
                .contenidoTexto(c.getContenidoTexto())
                .archivoPdfUrl(c.getArchivoPdfUrl())
                .fechaFirma(c.getFechaFirma())
                .fechaCreacion(c.getFechaCreacion())
                .fechaActualizacion(c.getFechaActualizacion())
                .build();
    }
}