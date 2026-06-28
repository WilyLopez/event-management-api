package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.auditoria.AuditoriaConstants;
import com.playzone.pems.application.auditoria.port.in.RegistrarLogUseCase;
import com.playzone.pems.application.comercial.dto.command.ActualizarPaqueteCommand;
import com.playzone.pems.application.comercial.dto.command.CrearPaqueteCommand;
import com.playzone.pems.application.comercial.dto.query.PaqueteEventoQuery;
import com.playzone.pems.application.comercial.port.in.GestionarPaquetesUseCase;
import com.playzone.pems.domain.comercial.model.PaqueteEvento;
import com.playzone.pems.domain.comercial.repository.PaqueteEventoRepository;
import com.playzone.pems.domain.comercial.repository.TipoEventoRepository;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
import com.playzone.pems.shared.exception.BusinessException;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaqueteService implements GestionarPaquetesUseCase {

    private final PaqueteEventoRepository repo;
    private final TipoEventoRepository    tipoEventoRepo;
    private final SupabaseAuthFacade      authFacade;
    private final RegistrarLogUseCase     auditoria;

    @Override
    public PaqueteEventoQuery crear(CrearPaqueteCommand command) {
        validarPaquete(command.getTipoEventoCodigo(), command.getColor());
        String slug = generarSlug(command.getNombre());
        if (repo.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        PaqueteEvento paquete = PaqueteEvento.builder()
                .nombre(command.getNombre())
                .slug(slug)
                .descripcionCorta(command.getDescripcionCorta())
                .descripcionLarga(command.getDescripcionLarga())
                .precio(command.getPrecio())
                .badge(command.getBadge())
                .colorHex(command.getColor())
                .imagenPath(command.getImagenUrl())
                .duracionMinutos(command.getDuracionMinutos())
                .limitePersonas(command.getLimitepersonas())
                .esActivo(true)
                .esDestacado(false)
                .orden(0)
                .tipoEventoCodigo(command.getTipoEventoCodigo())
                .beneficios(command.getBeneficios())
                .build();
        PaqueteEventoQuery resultado = toQuery(repo.save(paquete));
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_CREAR, AuditoriaConstants.MOD_COMERCIAL,
                "PaqueteEvento", resultado.getId(),
                null, resultado.getNombre(),
                "Paquete creado: " + resultado.getNombre(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
        return resultado;
    }

    @Override
    public PaqueteEventoQuery actualizar(ActualizarPaqueteCommand command) {
        validarPaquete(command.getTipoEventoCodigo(), command.getColor());
        PaqueteEvento existente = repo.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException("PaqueteEvento", command.getId()));
        PaqueteEvento actualizado = PaqueteEvento.builder()
                .id(existente.getId())
                .nombre(command.getNombre())
                .slug(existente.getSlug())
                .descripcionCorta(command.getDescripcionCorta())
                .descripcionLarga(command.getDescripcionLarga())
                .precio(command.getPrecio())
                .badge(command.getBadge())
                .colorHex(command.getColor())
                .imagenPath(command.getImagenUrl())
                .duracionMinutos(command.getDuracionMinutos())
                .limitePersonas(command.getLimitepersonas())
                .esActivo(command.isActivo())
                .esDestacado(command.isDestacado())
                .orden(command.getOrden())
                .tipoEventoCodigo(command.getTipoEventoCodigo())
                .beneficios(command.getBeneficios())
                .build();
        PaqueteEventoQuery resultado = toQuery(repo.save(actualizado));
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ACTUALIZAR, AuditoriaConstants.MOD_COMERCIAL,
                "PaqueteEvento", command.getId(),
                existente.getNombre(), resultado.getNombre(),
                "Paquete actualizado: " + resultado.getNombre(),
                null, null, AuditoriaConstants.NIVEL_INFO, AuditoriaConstants.RESULTADO_EXITOSO));
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public PaqueteEventoQuery obtenerPorId(Long id) {
        PaqueteEvento paquete = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaqueteEvento", id));
        return toQuery(paquete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteEventoQuery> listarTodos() {
        return repo.findAll().stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteEventoQuery> listarActivos() {
        return repo.findAllActivos().stream().map(this::toQuery).toList();
    }

    @Override
    public void eliminar(Long id) {
        PaqueteEvento paquete = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaqueteEvento", id));
        repo.deleteById(id);
        auditoria.ejecutar(new RegistrarLogUseCase.Command(
                authFacade.usuarioActualId().orElse(null),
                AuditoriaConstants.ACCION_ELIMINAR, AuditoriaConstants.MOD_COMERCIAL,
                "PaqueteEvento", id,
                paquete.getNombre(), null,
                "Paquete eliminado: " + paquete.getNombre(),
                null, null, AuditoriaConstants.NIVEL_CRITICAL, AuditoriaConstants.RESULTADO_EXITOSO));
    }

    @Override
    public PaqueteEventoQuery subirImagen(Long id, String url) {
        PaqueteEvento existente = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaqueteEvento", id));
        PaqueteEvento actualizado = PaqueteEvento.builder()
                .id(existente.getId())
                .nombre(existente.getNombre())
                .slug(existente.getSlug())
                .descripcionCorta(existente.getDescripcionCorta())
                .descripcionLarga(existente.getDescripcionLarga())
                .precio(existente.getPrecio())
                .badge(existente.getBadge())
                .colorHex(existente.getColorHex())
                .imagenPath(url)
                .duracionMinutos(existente.getDuracionMinutos())
                .limitePersonas(existente.getLimitePersonas())
                .esActivo(existente.isEsActivo())
                .esDestacado(existente.isEsDestacado())
                .orden(existente.getOrden())
                .tipoEventoCodigo(existente.getTipoEventoCodigo())
                .beneficios(existente.getBeneficios())
                .build();
        return toQuery(repo.save(actualizado));
    }

    private void validarPaquete(String tipoEventoCodigo, String colorHex) {
        validarTipoEvento(tipoEventoCodigo);
        if (colorHex != null && !colorHex.isBlank()) {
            if (!colorHex.matches("^#[0-9A-Fa-f]{6}$")) {
                throw new BusinessException("El color hexadecimal no es válido (debe tener el formato #RRGGBB).", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void validarTipoEvento(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new BusinessException("Debe seleccionar un tipo de evento.", HttpStatus.BAD_REQUEST);
        }
        if (!tipoEventoRepo.existePorCodigo(codigo)) {
            throw new BusinessException("Tipo de evento no válido: " + codigo, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public PaqueteEventoQuery reordenar(Long id, int nuevoOrden) {
        PaqueteEvento paquete = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaqueteEvento", id));
        List<PaqueteEvento> todos = repo.findAll();
        todos.stream()
                .filter(p -> p.getOrden() == nuevoOrden && !p.getId().equals(id))
                .findFirst()
                .ifPresent(otro -> {
                    PaqueteEvento intercambiado = PaqueteEvento.builder()
                            .id(otro.getId()).nombre(otro.getNombre()).slug(otro.getSlug())
                            .descripcionCorta(otro.getDescripcionCorta()).descripcionLarga(otro.getDescripcionLarga())
                            .precio(otro.getPrecio()).badge(otro.getBadge()).colorHex(otro.getColorHex())
                            .imagenPath(otro.getImagenPath()).duracionMinutos(otro.getDuracionMinutos())
                            .limitePersonas(otro.getLimitePersonas()).esActivo(otro.isEsActivo())
                            .esDestacado(otro.isEsDestacado()).orden(paquete.getOrden())
                            .tipoEventoCodigo(otro.getTipoEventoCodigo())
                            .beneficios(otro.getBeneficios()).build();
                    repo.save(intercambiado);
                });
        PaqueteEvento actualizado = PaqueteEvento.builder()
                .id(paquete.getId()).nombre(paquete.getNombre()).slug(paquete.getSlug())
                .descripcionCorta(paquete.getDescripcionCorta()).descripcionLarga(paquete.getDescripcionLarga())
                .precio(paquete.getPrecio()).badge(paquete.getBadge()).colorHex(paquete.getColorHex())
                .imagenPath(paquete.getImagenPath()).duracionMinutos(paquete.getDuracionMinutos())
                .limitePersonas(paquete.getLimitePersonas()).esActivo(paquete.isEsActivo())
                .esDestacado(paquete.isEsDestacado()).orden(nuevoOrden)
                .tipoEventoCodigo(paquete.getTipoEventoCodigo())
                .beneficios(paquete.getBeneficios()).build();
        return toQuery(repo.save(actualizado));
    }

    private String generarSlug(String nombre) {
        return nombre.toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-").replaceAll("^-|-$", "");
    }

    private PaqueteEventoQuery toQuery(PaqueteEvento p) {
        return PaqueteEventoQuery.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .slug(p.getSlug())
                .descripcionCorta(p.getDescripcionCorta())
                .descripcionLarga(p.getDescripcionLarga())
                .precio(p.getPrecio())
                .badge(p.getBadge())
                .color(p.getColorHex())
                .imagenUrl(p.getImagenPath())
                .duracionMinutos(p.getDuracionMinutos())
                .limitepersonas(p.getLimitePersonas())
                .activo(p.isEsActivo())
                .destacado(p.isEsDestacado())
                .orden(p.getOrden())
                .tipoEventoCodigo(p.getTipoEventoCodigo())
                .beneficios(p.getBeneficios())
                .fechaCreacion(p.getCreatedAt())
                .fechaActualizacion(p.getUpdatedAt())
                .build();
    }
}
