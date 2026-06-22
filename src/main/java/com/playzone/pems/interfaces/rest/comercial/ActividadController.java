package com.playzone.pems.interfaces.rest.comercial;

import com.playzone.pems.application.comercial.dto.command.ActualizarActividadCommand;
import com.playzone.pems.application.comercial.dto.command.CrearActividadCommand;
import com.playzone.pems.application.comercial.dto.query.ActividadLocalQuery;
import com.playzone.pems.application.comercial.port.in.GestionarActividadesUseCase;
import com.playzone.pems.interfaces.rest.comercial.response.ActividadLocalResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/actividades")
@RequiredArgsConstructor
public class ActividadController {

    private final GestionarActividadesUseCase useCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActividadLocalResponse>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarActivas().stream().map(this::toResponse).toList()));
    }

    @GetMapping("/especiales")
    public ResponseEntity<ApiResponse<List<ActividadLocalResponse>>> listarEspeciales() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarEspeciales().stream().map(this::toResponse).toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('sitio.actividad')")
    public ResponseEntity<ApiResponse<List<ActividadLocalResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarTodas().stream().map(this::toResponse).toList()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sitio.actividad')")
    public ResponseEntity<ApiResponse<ActividadLocalResponse>> crear(@Valid @RequestBody CrearActividadCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(useCase.crear(command))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sitio.actividad')")
    public ResponseEntity<ApiResponse<ActividadLocalResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarActividadCommand command) {
        ActualizarActividadCommand withId = ActualizarActividadCommand.builder()
                .id(id).nombre(command.getNombre()).descripcion(command.getDescripcion())
                .imagenUrl(command.getImagenUrl()).idZona(command.getIdZona())
                .esEspecial(command.isEsEspecial()).fechaInicio(command.getFechaInicio())
                .fechaFin(command.getFechaFin()).activa(command.isActiva())
                .destacada(command.isDestacada()).orden(command.getOrden()).build();
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.actualizar(withId))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sitio.actividad')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private ActividadLocalResponse toResponse(ActividadLocalQuery q) {
        return ActividadLocalResponse.builder()
                .id(q.getId()).nombre(q.getNombre()).descripcion(q.getDescripcion())
                .imagenUrl(q.getImagenUrl()).idZona(q.getIdZona()).nombreZona(q.getNombreZona())
                .esEspecial(q.isEsEspecial()).fechaInicio(q.getFechaInicio()).fechaFin(q.getFechaFin())
                .activa(q.isActiva()).destacada(q.isDestacada()).orden(q.getOrden())
                .fechaCreacion(q.getFechaCreacion()).fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}
