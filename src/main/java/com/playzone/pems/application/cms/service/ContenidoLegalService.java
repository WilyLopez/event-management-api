package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;
import com.playzone.pems.application.cms.port.in.GestionarContenidoLegalUseCase;
import com.playzone.pems.domain.cms.model.ContenidoLegal;
import com.playzone.pems.domain.cms.repository.ContenidoLegalRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenidoLegalService implements GestionarContenidoLegalUseCase {

    private final ContenidoLegalRepository legalRepository;

    @Override
    @Transactional(readOnly = true)
    public ContenidoLegalQuery obtenerPorTipo(String tipo) {
        return ContenidoLegalQuery.from(
                legalRepository.findActivoByTipo(tipo.toUpperCase())
                        .orElseThrow(() -> new ResourceNotFoundException("ContenidoLegal tipo=" + tipo, 0L)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContenidoLegalQuery> listar() {
        return legalRepository.findAll().stream().map(ContenidoLegalQuery::from).toList();
    }

    @Override
    @Transactional
    public ContenidoLegalQuery actualizar(ActualizarCommand command) {
        ContenidoLegal existente = legalRepository.findById(command.idContenidoLegal())
                .orElseThrow(() -> new ResourceNotFoundException("ContenidoLegal", command.idContenidoLegal()));

        ContenidoLegal actualizado = existente.toBuilder()
                .titulo(command.titulo())
                .contenido(command.contenido())
                .version(existente.getVersion() + 1)
                .idUsuarioEditor(command.idUsuario())
                .build();

        return ContenidoLegalQuery.from(legalRepository.save(actualizado));
    }
}
