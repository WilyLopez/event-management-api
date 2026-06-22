package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;
import com.playzone.pems.application.cms.port.in.GestionarContenidoLegalUseCase;
import com.playzone.pems.domain.cms.model.ContenidoLegal;
import com.playzone.pems.domain.cms.repository.ContenidoLegalRepository;
import com.playzone.pems.shared.exception.BusinessException;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContenidoLegalService implements GestionarContenidoLegalUseCase {

    private static final Set<String> TIPOS_SISTEMA =
            Set.of("TERMINOS", "PRIVACIDAD");

    private final ContenidoLegalRepository legalRepository;

    @Override
    @Transactional(readOnly = true)
    public ContenidoLegalQuery obtenerPorTipo(String tipo) {
        return ContenidoLegalQuery.from(
                legalRepository.findActivoByTipo(tipo.toUpperCase())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "ContenidoLegal tipo=" + tipo)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContenidoLegalQuery> listar() {
        return legalRepository.findAll().stream().map(ContenidoLegalQuery::from).toList();
    }

    @Override
    @Transactional
    public ContenidoLegalQuery crear(CrearCommand command) {
        String tipo = command.tipo().toUpperCase();
        if (legalRepository.findByTipo(tipo).isPresent()) {
            throw new BusinessException("Ya existe un documento legal de tipo: " + tipo);
        }
        ContenidoLegal nuevo = ContenidoLegal.builder()
                .tipo(tipo)
                .titulo(command.titulo())
                .contenido(command.contenido() != null ? command.contenido() : "")
                .version(1)
                .activo(true)
                .idUsuarioEditor(command.idUsuario())
                .build();
        return ContenidoLegalQuery.from(legalRepository.save(nuevo));
    }

    @Override
    @Transactional
    public ContenidoLegalQuery actualizar(ActualizarCommand command) {
        ContenidoLegal existente = legalRepository.findByTipo(command.tipo().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContenidoLegal tipo=" + command.tipo()));

        ContenidoLegal actualizado = existente.toBuilder()
                .titulo(command.titulo())
                .contenido(command.contenido())
                .version(existente.getVersion() + 1)
                .idUsuarioEditor(command.idUsuario())
                .build();

        return ContenidoLegalQuery.from(legalRepository.save(actualizado));
    }

    @Override
    @Transactional
    public ContenidoLegalQuery activar(String tipo) {
        ContenidoLegal doc = legalRepository.findByTipo(tipo.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContenidoLegal tipo=" + tipo));
        return ContenidoLegalQuery.from(
                legalRepository.save(doc.toBuilder().activo(true).build()));
    }

    @Override
    @Transactional
    public ContenidoLegalQuery desactivar(String tipo) {
        String tipoUpper = tipo.toUpperCase();
        if (TIPOS_SISTEMA.contains(tipoUpper)) {
            throw new BusinessException(
                    "El documento '" + tipoUpper + "' es parte del sistema y no puede desactivarse.");
        }
        ContenidoLegal doc = legalRepository.findByTipo(tipoUpper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContenidoLegal tipo=" + tipo));
        return ContenidoLegalQuery.from(
                legalRepository.save(doc.toBuilder().activo(false).build()));
    }

    @Override
    @Transactional
    public void eliminar(String tipo) {
        String tipoUpper = tipo.toUpperCase();
        if (TIPOS_SISTEMA.contains(tipoUpper)) {
            throw new BusinessException(
                    "El documento '" + tipoUpper + "' es parte del sistema y no puede eliminarse.");
        }
        legalRepository.findByTipo(tipoUpper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ContenidoLegal tipo=" + tipo));
        legalRepository.deleteByTipo(tipoUpper);
    }
}
