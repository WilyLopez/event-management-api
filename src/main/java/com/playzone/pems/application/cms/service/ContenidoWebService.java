package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.command.EditarContenidoCommand;
import com.playzone.pems.application.cms.dto.query.ContenidoWebQuery;
import com.playzone.pems.application.cms.port.in.EditarContenidoWebUseCase;
import com.playzone.pems.domain.cms.exception.ContenidoNotFoundException;
import com.playzone.pems.domain.cms.model.ContenidoWeb;
import com.playzone.pems.domain.cms.repository.ContenidoWebRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContenidoWebService implements EditarContenidoWebUseCase {

    private final ContenidoWebRepository contenidoRepository;

    @Override
    @Transactional
    public ContenidoWebQuery ejecutar(EditarContenidoCommand command) {
        ContenidoWeb contenido = contenidoRepository.findById(command.getIdContenido())
                .orElseThrow(() -> new ContenidoNotFoundException(command.getIdContenido()));

        ContenidoWeb actualizado = contenido.toBuilder()
                .valorEs(command.getValorEs())
                .valorEn(command.getValorEn())
                .idUsuarioEditor(command.getIdUsuarioEditor())
                .build();

        return toQuery(contenidoRepository.save(actualizado));
    }

    private ContenidoWebQuery toQuery(ContenidoWeb c) {
        return ContenidoWebQuery.builder()
                .id(c.getId())
                .idSeccion(c.getIdSeccion())
                .idTipoContenido(c.getIdTipoContenido())
                .clave(c.getClave())
                .valorEs(c.getValorEs())
                .valorEn(c.getValorEn())
                .activo(c.isActivo())
                .fechaActualizacion(c.getFechaActualizacion())
                .build();
    }
}