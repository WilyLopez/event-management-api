package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.comercial.dto.command.ActualizarPaqueteCommand;
import com.playzone.pems.application.comercial.dto.command.CrearPaqueteCommand;
import com.playzone.pems.application.comercial.dto.query.PaqueteEventoQuery;
import com.playzone.pems.application.comercial.port.in.GestionarPaquetesUseCase;
import com.playzone.pems.domain.comercial.model.PaqueteEvento;
import com.playzone.pems.domain.comercial.repository.PaqueteEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaqueteService implements GestionarPaquetesUseCase {

    private final PaqueteEventoRepository repo;

    @Override
    public PaqueteEventoQuery crear(CrearPaqueteCommand command) {
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
                .color(command.getColor())
                .imagenUrl(command.getImagenUrl())
                .duracionMinutos(command.getDuracionMinutos())
                .limitepersonas(command.getLimitepersonas())
                .activo(true)
                .destacado(false)
                .orden(0)
                .beneficios(command.getBeneficios())
                .build();
        return toQuery(repo.save(paquete));
    }

    @Override
    public PaqueteEventoQuery actualizar(ActualizarPaqueteCommand command) {
        PaqueteEvento existente = repo.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado: " + command.getId()));
        PaqueteEvento actualizado = PaqueteEvento.builder()
                .id(existente.getId())
                .nombre(command.getNombre())
                .slug(existente.getSlug())
                .descripcionCorta(command.getDescripcionCorta())
                .descripcionLarga(command.getDescripcionLarga())
                .precio(command.getPrecio())
                .badge(command.getBadge())
                .color(command.getColor())
                .imagenUrl(command.getImagenUrl())
                .duracionMinutos(command.getDuracionMinutos())
                .limitepersonas(command.getLimitepersonas())
                .activo(command.isActivo())
                .destacado(command.isDestacado())
                .orden(command.getOrden())
                .beneficios(command.getBeneficios())
                .build();
        return toQuery(repo.save(actualizado));
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
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado: " + id));
        repo.deleteById(id);
    }

    @Override
    public PaqueteEventoQuery subirImagen(Long id, String url) {
        PaqueteEvento existente = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado: " + id));
        PaqueteEvento actualizado = PaqueteEvento.builder()
                .id(existente.getId())
                .nombre(existente.getNombre())
                .slug(existente.getSlug())
                .descripcionCorta(existente.getDescripcionCorta())
                .descripcionLarga(existente.getDescripcionLarga())
                .precio(existente.getPrecio())
                .badge(existente.getBadge())
                .color(existente.getColor())
                .imagenUrl(url)
                .duracionMinutos(existente.getDuracionMinutos())
                .limitepersonas(existente.getLimitepersonas())
                .activo(existente.isActivo())
                .destacado(existente.isDestacado())
                .orden(existente.getOrden())
                .beneficios(existente.getBeneficios())
                .build();
        return toQuery(repo.save(actualizado));
    }

    @Override
    public PaqueteEventoQuery reordenar(Long id, int nuevoOrden) {
        PaqueteEvento paquete = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado: " + id));
        List<PaqueteEvento> todos = repo.findAll();
        todos.stream()
                .filter(p -> p.getOrden() == nuevoOrden && !p.getId().equals(id))
                .findFirst()
                .ifPresent(otro -> {
                    PaqueteEvento intercambiado = PaqueteEvento.builder()
                            .id(otro.getId()).nombre(otro.getNombre()).slug(otro.getSlug())
                            .descripcionCorta(otro.getDescripcionCorta()).descripcionLarga(otro.getDescripcionLarga())
                            .precio(otro.getPrecio()).badge(otro.getBadge()).color(otro.getColor())
                            .imagenUrl(otro.getImagenUrl()).duracionMinutos(otro.getDuracionMinutos())
                            .limitepersonas(otro.getLimitepersonas()).activo(otro.isActivo())
                            .destacado(otro.isDestacado()).orden(paquete.getOrden())
                            .beneficios(otro.getBeneficios()).build();
                    repo.save(intercambiado);
                });
        PaqueteEvento actualizado = PaqueteEvento.builder()
                .id(paquete.getId()).nombre(paquete.getNombre()).slug(paquete.getSlug())
                .descripcionCorta(paquete.getDescripcionCorta()).descripcionLarga(paquete.getDescripcionLarga())
                .precio(paquete.getPrecio()).badge(paquete.getBadge()).color(paquete.getColor())
                .imagenUrl(paquete.getImagenUrl()).duracionMinutos(paquete.getDuracionMinutos())
                .limitepersonas(paquete.getLimitepersonas()).activo(paquete.isActivo())
                .destacado(paquete.isDestacado()).orden(nuevoOrden)
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
                .color(p.getColor())
                .imagenUrl(p.getImagenUrl())
                .duracionMinutos(p.getDuracionMinutos())
                .limitepersonas(p.getLimitepersonas())
                .activo(p.isActivo())
                .destacado(p.isDestacado())
                .orden(p.getOrden())
                .beneficios(p.getBeneficios())
                .fechaCreacion(p.getFechaCreacion())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }
}
