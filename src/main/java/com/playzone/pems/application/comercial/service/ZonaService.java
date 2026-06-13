package com.playzone.pems.application.comercial.service;

import com.playzone.pems.application.comercial.dto.command.ActualizarZonaCommand;
import com.playzone.pems.application.comercial.dto.command.CrearZonaCommand;
import com.playzone.pems.application.comercial.dto.query.ZonaJuegoQuery;
import com.playzone.pems.application.comercial.port.in.GestionarZonasUseCase;
import com.playzone.pems.domain.comercial.model.ZonaJuego;
import com.playzone.pems.domain.comercial.repository.ZonaJuegoRepository;
import com.playzone.pems.domain.storage.StoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class ZonaService implements GestionarZonasUseCase {

    private static final Pattern VIDEO_PATTERN = Pattern.compile(
            "^https://(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/|tiktok\\.com/).+"
    );

    private final ZonaJuegoRepository repo;
    private final StoragePort         storagePort;

    @Override
    public ZonaJuegoQuery crear(CrearZonaCommand command) {
        String slug = generarSlug(command.getNombre());
        if (repo.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        validarVideos(command.getVideos());
        ZonaJuego zona = ZonaJuego.builder()
                .nombre(command.getNombre())
                .slug(slug)
                .descripcion(command.getDescripcion())
                .edadMinima(command.getEdadMinima())
                .edadMaxima(command.getEdadMaxima())
                .activa(true)
                .destacada(false)
                .orden(0)
                .imagenes(command.getImagenes() != null ? command.getImagenes() : List.of())
                .videos(command.getVideos() != null ? command.getVideos() : List.of())
                .build();
        return toQuery(repo.save(zona));
    }

    @Override
    public ZonaJuegoQuery actualizar(ActualizarZonaCommand command) {
        ZonaJuego existente = repo.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada: " + command.getId()));
        validarVideos(command.getVideos());
        ZonaJuego actualizada = ZonaJuego.builder()
                .id(existente.getId())
                .nombre(command.getNombre())
                .slug(existente.getSlug())
                .descripcion(command.getDescripcion())
                .edadMinima(command.getEdadMinima())
                .edadMaxima(command.getEdadMaxima())
                .activa(command.isActiva())
                .destacada(command.isDestacada())
                .orden(command.getOrden())
                .imagenes(command.getImagenes() != null ? command.getImagenes() : existente.getImagenes())
                .videos(command.getVideos() != null ? command.getVideos() : existente.getVideos())
                .build();
        return toQuery(repo.save(actualizada));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZonaJuegoQuery> listarTodas() {
        return repo.findAll().stream().map(this::toQuery).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZonaJuegoQuery> listarActivas() {
        return repo.findAllActivas().stream().map(this::toQuery).toList();
    }

    @Override
    public void eliminar(Long id) {
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Zona no encontrada: " + id));
        repo.deleteById(id);
    }

    @Override
    public ZonaJuegoQuery agregarMedia(Long id, String url, String tipo) {
        ZonaJuego zona = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada: " + id));
        String tipoDetectado = (tipo != null && !tipo.isBlank()) ? tipo.toUpperCase()
                : (esVideo(url) ? "VIDEO" : "IMAGEN");
        List<String> imagenes = new ArrayList<>(zona.getImagenes() != null ? zona.getImagenes() : List.of());
        List<String> videos   = new ArrayList<>(zona.getVideos() != null ? zona.getVideos() : List.of());
        if ("VIDEO".equals(tipoDetectado)) {
            videos.add(url);
        } else {
            imagenes.add(url);
        }
        ZonaJuego actualizada = ZonaJuego.builder()
                .id(zona.getId()).nombre(zona.getNombre()).slug(zona.getSlug())
                .descripcion(zona.getDescripcion()).edadMinima(zona.getEdadMinima())
                .edadMaxima(zona.getEdadMaxima()).activa(zona.isActiva())
                .destacada(zona.isDestacada()).orden(zona.getOrden())
                .imagenes(imagenes).videos(videos).build();
        return toQuery(repo.save(actualizada));
    }

    @Override
    public ZonaJuegoQuery eliminarMedia(Long id, String url) {
        ZonaJuego zona = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada: " + id));
        boolean esImagen = !esVideo(url);
        if (esImagen) {
            storagePort.deleteByUrl(url);
        }
        List<String> imagenes = new ArrayList<>(zona.getImagenes() != null ? zona.getImagenes() : List.of());
        List<String> videos   = new ArrayList<>(zona.getVideos() != null ? zona.getVideos() : List.of());
        imagenes.remove(url);
        videos.remove(url);
        ZonaJuego actualizada = ZonaJuego.builder()
                .id(zona.getId()).nombre(zona.getNombre()).slug(zona.getSlug())
                .descripcion(zona.getDescripcion()).edadMinima(zona.getEdadMinima())
                .edadMaxima(zona.getEdadMaxima()).activa(zona.isActiva())
                .destacada(zona.isDestacada()).orden(zona.getOrden())
                .imagenes(imagenes).videos(videos).build();
        return toQuery(repo.save(actualizada));
    }

    @Override
    public ZonaJuegoQuery reordenar(Long id, int nuevoOrden) {
        ZonaJuego zona = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada: " + id));
        repo.findAll().stream()
                .filter(z -> z.getOrden() == nuevoOrden && !z.getId().equals(id))
                .findFirst()
                .ifPresent(otra -> repo.save(ZonaJuego.builder()
                        .id(otra.getId()).nombre(otra.getNombre()).slug(otra.getSlug())
                        .descripcion(otra.getDescripcion()).edadMinima(otra.getEdadMinima())
                        .edadMaxima(otra.getEdadMaxima()).activa(otra.isActiva())
                        .destacada(otra.isDestacada()).orden(zona.getOrden())
                        .imagenes(otra.getImagenes()).videos(otra.getVideos()).build()));
        ZonaJuego actualizada = ZonaJuego.builder()
                .id(zona.getId()).nombre(zona.getNombre()).slug(zona.getSlug())
                .descripcion(zona.getDescripcion()).edadMinima(zona.getEdadMinima())
                .edadMaxima(zona.getEdadMaxima()).activa(zona.isActiva())
                .destacada(zona.isDestacada()).orden(nuevoOrden)
                .imagenes(zona.getImagenes()).videos(zona.getVideos()).build();
        return toQuery(repo.save(actualizada));
    }

    private boolean esVideo(String url) {
        return url != null && VIDEO_PATTERN.matcher(url).matches();
    }

    private void validarVideos(List<String> videos) {
        if (videos == null) return;
        for (String url : videos) {
            if (!esVideo(url)) {
                throw new IllegalArgumentException("URL de video no valida: " + url);
            }
        }
    }

    private String generarSlug(String nombre) {
        return nombre.toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-").replaceAll("^-|-$", "");
    }

    private ZonaJuegoQuery toQuery(ZonaJuego z) {
        return ZonaJuegoQuery.builder()
                .id(z.getId()).nombre(z.getNombre()).slug(z.getSlug())
                .descripcion(z.getDescripcion()).edadMinima(z.getEdadMinima())
                .edadMaxima(z.getEdadMaxima()).activa(z.isActiva())
                .destacada(z.isDestacada()).orden(z.getOrden())
                .imagenes(z.getImagenes()).videos(z.getVideos())
                .fechaCreacion(z.getFechaCreacion()).fechaActualizacion(z.getFechaActualizacion())
                .build();
    }
}
