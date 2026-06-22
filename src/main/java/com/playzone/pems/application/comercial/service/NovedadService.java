package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.comercial.dto.command.ActualizarNovedadCommand;
import com.playzone.pems.application.comercial.dto.command.CrearNovedadCommand;
import com.playzone.pems.application.comercial.dto.query.NovedadLocalQuery;
import com.playzone.pems.application.comercial.port.in.GestionarNovedadesUseCase;
import com.playzone.pems.domain.comercial.model.NovedadLocal;
import com.playzone.pems.domain.comercial.repository.NovedadLocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NovedadService implements GestionarNovedadesUseCase {

    private final NovedadLocalRepository repo;

    @Override
    public NovedadLocalQuery crear(CrearNovedadCommand command) {
        NovedadLocal novedad = NovedadLocal.builder()
                .titulo(command.getTitulo())
                .descripcion(command.getDescripcion())
                .imagenUrl(command.getImagenUrl())
                .textoCta(command.getTextoCta())
                .urlCta(command.getUrlCta())
                .prioridad(command.getPrioridad())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .visibleHome(command.isVisibleHome())
                .destacada(command.isDestacada())
                .activa(true)
                .build();
        return toQuery(repo.save(novedad));
    }

    @Override
    public NovedadLocalQuery actualizar(ActualizarNovedadCommand command) {
        repo.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Novedad no encontrada: " + command.getId()));
        NovedadLocal actualizada = NovedadLocal.builder()
                .id(command.getId())
                .titulo(command.getTitulo())
                .descripcion(command.getDescripcion())
                .imagenUrl(command.getImagenUrl())
                .textoCta(command.getTextoCta())
                .urlCta(command.getUrlCta())
                .prioridad(command.getPrioridad())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .visibleHome(command.isVisibleHome())
                .destacada(command.isDestacada())
                .activa(command.isActiva())
                .build();
        return toQuery(repo.save(actualizada));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NovedadLocalQuery> listarTodas() {
        return repo.findAllActivas().stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NovedadLocalQuery> listarActivas() {
        return repo.findAllActivas().stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NovedadLocalQuery> listarVisiblesHome() {
        return repo.findVisiblesHome().stream().limit(3).map(this::toQuery).toList();
    }

    @Override
    public void eliminar(Long id) {
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Novedad no encontrada: " + id));
        repo.deleteById(id);
    }

    private NovedadLocalQuery toQuery(NovedadLocal n) {
        return NovedadLocalQuery.builder()
                .id(n.getId()).titulo(n.getTitulo()).descripcion(n.getDescripcion())
                .imagenUrl(n.getImagenUrl()).textoCta(n.getTextoCta()).urlCta(n.getUrlCta())
                .prioridad(n.getPrioridad()).fechaInicio(n.getFechaInicio()).fechaFin(n.getFechaFin())
                .visibleHome(n.isVisibleHome()).destacada(n.isDestacada()).activa(n.isActiva())
                .fechaCreacion(n.getFechaCreacion()).fechaActualizacion(n.getFechaActualizacion())
                .build();
    }
}
