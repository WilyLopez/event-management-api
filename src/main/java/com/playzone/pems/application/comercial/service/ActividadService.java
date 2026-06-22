package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.comercial.dto.command.ActualizarActividadCommand;
import com.playzone.pems.application.comercial.dto.command.CrearActividadCommand;
import com.playzone.pems.application.comercial.dto.query.ActividadLocalQuery;
import com.playzone.pems.application.comercial.port.in.GestionarActividadesUseCase;
import com.playzone.pems.domain.comercial.model.ActividadLocal;
import com.playzone.pems.domain.comercial.repository.ActividadLocalRepository;
import com.playzone.pems.domain.comercial.repository.ZonaJuegoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActividadService implements GestionarActividadesUseCase {

    private final ActividadLocalRepository repo;
    private final ZonaJuegoRepository      zonaRepo;

    @Override
    public ActividadLocalQuery crear(CrearActividadCommand command) {
        String nombreZona = resolverNombreZona(command.getIdZona());
        ActividadLocal actividad = ActividadLocal.builder()
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .imagenUrl(command.getImagenUrl())
                .idZona(command.getIdZona())
                .nombreZona(nombreZona)
                .esEspecial(command.isEsEspecial())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .activa(true)
                .destacada(false)
                .orden(0)
                .build();
        return toQuery(repo.save(actividad));
    }

    @Override
    public ActividadLocalQuery actualizar(ActualizarActividadCommand command) {
        repo.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada: " + command.getId()));
        String nombreZona = resolverNombreZona(command.getIdZona());
        ActividadLocal actualizada = ActividadLocal.builder()
                .id(command.getId())
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .imagenUrl(command.getImagenUrl())
                .idZona(command.getIdZona())
                .nombreZona(nombreZona)
                .esEspecial(command.isEsEspecial())
                .fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin())
                .activa(command.isActiva())
                .destacada(command.isDestacada())
                .orden(command.getOrden())
                .build();
        return toQuery(repo.save(actualizada));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadLocalQuery> listarTodas() {
        return repo.findAllActivas().stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadLocalQuery> listarActivas() {
        return repo.findAllActivas().stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadLocalQuery> listarEspeciales() {
        return repo.findEspeciales().stream().map(this::toQuery).toList();
    }

    @Override
    public void eliminar(Long id) {
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada: " + id));
        repo.deleteById(id);
    }

    private String resolverNombreZona(Long idZona) {
        if (idZona == null) return null;
        return zonaRepo.findById(idZona).map(z -> z.getNombre()).orElse(null);
    }

    private ActividadLocalQuery toQuery(ActividadLocal a) {
        return ActividadLocalQuery.builder()
                .id(a.getId()).nombre(a.getNombre()).descripcion(a.getDescripcion())
                .imagenUrl(a.getImagenUrl()).idZona(a.getIdZona()).nombreZona(a.getNombreZona())
                .esEspecial(a.isEsEspecial()).fechaInicio(a.getFechaInicio()).fechaFin(a.getFechaFin())
                .activa(a.isActiva()).destacada(a.isDestacada()).orden(a.getOrden())
                .fechaCreacion(a.getFechaCreacion()).fechaActualizacion(a.getFechaActualizacion())
                .build();
    }
}
