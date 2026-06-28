package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.comercial.dto.command.ActualizarActividadCommand;
import com.playzone.pems.application.comercial.dto.command.CrearActividadCommand;
import com.playzone.pems.application.comercial.dto.query.ActividadLocalQuery;
import com.playzone.pems.application.comercial.port.in.GestionarActividadesUseCase;
import com.playzone.pems.domain.comercial.model.ActividadLocal;
import com.playzone.pems.domain.comercial.repository.ActividadLocalRepository;
import com.playzone.pems.domain.comercial.repository.ZonaJuegoRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
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
    private final SupabaseAuthFacade       authFacade;
    private final RegistrarLogUseCase      auditoria;

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
        ActividadLocalQuery resultado = toQuery(repo.save(actividad));
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_CREAR, AuditoriaConstants.MOD_COMERCIAL,
                "ActividadLocal", resultado.getId(),
                null, resultado.getNombre(),
                "Actividad creada: " + resultado.getNombre(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
        return resultado;
    }

    @Override
    public ActividadLocalQuery actualizar(ActualizarActividadCommand command) {
        repo.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ActividadLocal", command.getId()));
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
        ActividadLocalQuery resultado = toQuery(repo.save(actualizada));
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_COMERCIAL,
                "ActividadLocal", command.getId(),
                null, resultado.getNombre(),
                "Actividad actualizada: " + resultado.getNombre(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
        return resultado;
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
        ActividadLocal actividad = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ActividadLocal", id));
        repo.deleteById(id);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ELIMINAR, AuditoriaConstants.MOD_COMERCIAL,
                "ActividadLocal", id,
                actividad.getNombre(), null,
                "Actividad eliminada: " + actividad.getNombre(),
                null, null, AuditoriaConstants.NIVEL_CRITICAL, AuditoriaConstants.RESULTADO_EXITOSO));
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
