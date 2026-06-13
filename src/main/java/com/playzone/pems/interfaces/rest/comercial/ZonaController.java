package com.playzone.pems.interfaces.rest.comercial;

import com.playzone.pems.application.comercial.dto.command.ActualizarZonaCommand;
import com.playzone.pems.application.comercial.dto.command.CrearZonaCommand;
import com.playzone.pems.application.comercial.dto.query.ZonaJuegoQuery;
import com.playzone.pems.application.comercial.port.in.GestionarZonasUseCase;
import com.playzone.pems.interfaces.rest.comercial.response.ZonaJuegoResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/zonas")
@RequiredArgsConstructor
public class ZonaController {

    private final GestionarZonasUseCase useCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ZonaJuegoResponse>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarActivas().stream().map(this::toResponse).toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('sitio.zona')")
    public ResponseEntity<ApiResponse<List<ZonaJuegoResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarTodas().stream().map(this::toResponse).toList()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sitio.zona')")
    public ResponseEntity<ApiResponse<ZonaJuegoResponse>> crear(@Valid @RequestBody CrearZonaCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(useCase.crear(command))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sitio.zona')")
    public ResponseEntity<ApiResponse<ZonaJuegoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarZonaCommand command) {
        ActualizarZonaCommand withId = ActualizarZonaCommand.builder()
                .id(id).nombre(command.getNombre()).descripcion(command.getDescripcion())
                .edadMinima(command.getEdadMinima()).edadMaxima(command.getEdadMaxima())
                .activa(command.isActiva()).destacada(command.isDestacada()).orden(command.getOrden())
                .imagenes(command.getImagenes()).videos(command.getVideos()).build();
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.actualizar(withId))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sitio.zona')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping("/{id}/media")
    @PreAuthorize("hasAuthority('sitio.zona')")
    public ResponseEntity<ApiResponse<ZonaJuegoResponse>> agregarMedia(
            @PathVariable Long id,
            @RequestParam String url,
            @RequestParam(defaultValue = "") String tipo) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.agregarMedia(id, url, tipo))));
    }

    @DeleteMapping("/{id}/media")
    @PreAuthorize("hasAuthority('sitio.zona')")
    public ResponseEntity<ApiResponse<ZonaJuegoResponse>> eliminarMedia(
            @PathVariable Long id,
            @RequestParam String url) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.eliminarMedia(id, url))));
    }

    @PatchMapping("/{id}/orden")
    @PreAuthorize("hasAuthority('sitio.zona')")
    public ResponseEntity<ApiResponse<ZonaJuegoResponse>> reordenar(
            @PathVariable Long id,
            @RequestParam int nuevoOrden) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.reordenar(id, nuevoOrden))));
    }

    private ZonaJuegoResponse toResponse(ZonaJuegoQuery q) {
        return ZonaJuegoResponse.builder()
                .id(q.getId()).nombre(q.getNombre()).slug(q.getSlug())
                .descripcion(q.getDescripcion()).edadMinima(q.getEdadMinima())
                .edadMaxima(q.getEdadMaxima()).activa(q.isActiva()).destacada(q.isDestacada())
                .orden(q.getOrden()).imagenes(q.getImagenes()).videos(q.getVideos())
                .fechaCreacion(q.getFechaCreacion()).fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}
