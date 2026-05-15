package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.query.SeccionWebQuery;
import com.playzone.pems.application.cms.port.in.GestionarSeccionWebUseCase;
import com.playzone.pems.domain.cms.model.SeccionWeb;
import com.playzone.pems.domain.cms.repository.SeccionWebRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeccionWebService implements GestionarSeccionWebUseCase {

    private final SeccionWebRepository seccionRepository;

    @Override
    @Transactional
    public SeccionWebQuery crear(CrearCommand command) {
        if (seccionRepository.existsByCodigo(command.codigo().toUpperCase())) {
            throw new ValidationException("codigo", "Ya existe una sección con ese código.");
        }
        SeccionWeb seccion = SeccionWeb.builder()
                .codigo(command.codigo().toUpperCase())
                .nombre(command.nombre())
                .descripcion(command.descripcion())
                .ordenVisualizacion(command.ordenVisualizacion())
                .visible(true)
                .build();
        return SeccionWebQuery.from(seccionRepository.save(seccion));
    }

    @Override
    @Transactional
    public SeccionWebQuery actualizar(ActualizarCommand command) {
        SeccionWeb existente = findOrThrow(command.idSeccion());
        SeccionWeb actualizado = existente.toBuilder()
                .nombre(command.nombre())
                .descripcion(command.descripcion())
                .ordenVisualizacion(command.ordenVisualizacion())
                .build();
        return SeccionWebQuery.from(seccionRepository.save(actualizado));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionWebQuery> listar() {
        return seccionRepository.findAll().stream().map(SeccionWebQuery::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionWebQuery> listarVisibles() {
        return seccionRepository.findVisibles().stream().map(SeccionWebQuery::from).toList();
    }

    @Override
    @Transactional
    public void activar(Long idSeccion) {
        SeccionWeb s = findOrThrow(idSeccion);
        seccionRepository.save(s.toBuilder().visible(true).build());
    }

    @Override
    @Transactional
    public void desactivar(Long idSeccion) {
        SeccionWeb s = findOrThrow(idSeccion);
        seccionRepository.save(s.toBuilder().visible(false).build());
    }

    @Override
    @Transactional
    public void eliminar(Long idSeccion) {
        findOrThrow(idSeccion);
        seccionRepository.deleteById(idSeccion);
    }

    private SeccionWeb findOrThrow(Long id) {
        return seccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SeccionWeb", id));
    }
}
