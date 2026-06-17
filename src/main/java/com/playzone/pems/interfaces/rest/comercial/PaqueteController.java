package com.playzone.pems.interfaces.rest.comercial;

import com.playzone.pems.application.comercial.dto.command.ActualizarPaqueteCommand;
import com.playzone.pems.application.comercial.dto.command.CrearPaqueteCommand;
import com.playzone.pems.application.comercial.dto.query.PaqueteEventoQuery;
import com.playzone.pems.application.comercial.port.in.GestionarPaquetesUseCase;
import com.playzone.pems.application.comercial.port.in.GestionarExtrasUseCase;
import com.playzone.pems.domain.comercial.model.ExtraPaquete;
import com.playzone.pems.interfaces.rest.comercial.response.PaqueteEventoResponse;
import com.playzone.pems.interfaces.rest.comercial.response.ExtraPaqueteResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/paquetes")
@RequiredArgsConstructor
public class PaqueteController {

    private final GestionarPaquetesUseCase useCase;
    private final GestionarExtrasUseCase extrasUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaqueteEventoResponse>>> listarActivos() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarActivos().stream().map(this::toResponse).toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<List<PaqueteEventoResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarTodos().stream().map(this::toResponse).toList()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<PaqueteEventoResponse>> crear(@Valid @RequestBody CrearPaqueteCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(useCase.crear(command))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<PaqueteEventoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPaqueteCommand command) {
        ActualizarPaqueteCommand withId = ActualizarPaqueteCommand.builder()
                .id(id).nombre(command.getNombre()).descripcionCorta(command.getDescripcionCorta())
                .descripcionLarga(command.getDescripcionLarga()).precio(command.getPrecio())
                .badge(command.getBadge()).color(command.getColor()).imagenUrl(command.getImagenUrl())
                .duracionMinutos(command.getDuracionMinutos()).limitepersonas(command.getLimitepersonas())
                .activo(command.isActivo()).destacado(command.isDestacado()).orden(command.getOrden())
                .tipoEventoCodigo(command.getTipoEventoCodigo())
                .beneficios(command.getBeneficios()).build();
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.actualizar(withId))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{id}/orden")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<PaqueteEventoResponse>> reordenar(
            @PathVariable Long id,
            @RequestParam int nuevoOrden) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.reordenar(id, nuevoOrden))));
    }

    // ── Extras (Opcionales) ──────────────────────────────────────────────────

    @GetMapping("/{idPaquete}/extras")
    public ResponseEntity<ApiResponse<List<ExtraPaqueteResponse>>> listarExtras(@PathVariable Long idPaquete) {
        return ResponseEntity.ok(ApiResponse.ok(
                extrasUseCase.listarActivosPorPaquete(idPaquete).stream().map(this::toExtraResponse).toList()));
    }

    @PostMapping("/{idPaquete}/extras")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<ExtraPaqueteResponse>> crearExtra(
            @PathVariable Long idPaquete,
            @RequestBody ExtraPaquete extra) {
        ExtraPaquete nuevo = extra.toBuilder().idPaquete(idPaquete).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toExtraResponse(extrasUseCase.crear(nuevo))));
    }

    @PutMapping("/{idPaquete}/extras/{id}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<ExtraPaqueteResponse>> actualizarExtra(
            @PathVariable Long idPaquete,
            @PathVariable Long id,
            @RequestBody ExtraPaquete extra) {
        ExtraPaquete actualizado = extra.toBuilder().id(id).idPaquete(idPaquete).build();
        return ResponseEntity.ok(ApiResponse.ok(toExtraResponse(extrasUseCase.actualizar(actualizado))));
    }

    @DeleteMapping("/{idPaquete}/extras/{id}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<Void>> eliminarExtra(@PathVariable Long idPaquete, @PathVariable Long id) {
        extrasUseCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private PaqueteEventoResponse toResponse(PaqueteEventoQuery q) {
        return PaqueteEventoResponse.builder()
                .id(q.getId()).nombre(q.getNombre()).slug(q.getSlug())
                .descripcionCorta(q.getDescripcionCorta()).descripcionLarga(q.getDescripcionLarga())
                .precio(q.getPrecio()).badge(q.getBadge()).color(q.getColor())
                .imagenUrl(q.getImagenUrl()).duracionMinutos(q.getDuracionMinutos())
                .limitepersonas(q.getLimitepersonas()).activo(q.isActivo()).destacado(q.isDestacado())
                .orden(q.getOrden()).tipoEventoCodigo(q.getTipoEventoCodigo())
                .beneficios(q.getBeneficios())
                .fechaCreacion(q.getFechaCreacion()).fechaActualizacion(q.getFechaActualizacion())
                .build();
    }

    private ExtraPaqueteResponse toExtraResponse(ExtraPaquete e) {
        return ExtraPaqueteResponse.builder()
                .id(e.getId()).idPaquete(e.getIdPaquete()).nombre(e.getNombre())
                .descripcion(e.getDescripcion()).activo(e.isActivo()).orden(e.getOrden())
                .build();
    }
}
