package com.playzone.pems.interfaces.rest.comercial;

import com.playzone.pems.application.comercial.dto.command.ActualizarNovedadCommand;
import com.playzone.pems.application.comercial.dto.command.CrearNovedadCommand;
import com.playzone.pems.application.comercial.dto.query.NovedadLocalQuery;
import com.playzone.pems.application.comercial.port.in.GestionarNovedadesUseCase;
import com.playzone.pems.interfaces.rest.comercial.response.NovedadLocalResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/novedades")
@RequiredArgsConstructor
public class NovedadController {

    private final GestionarNovedadesUseCase useCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NovedadLocalResponse>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarActivas().stream().map(this::toResponse).toList()));
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<List<NovedadLocalResponse>>> listarHome() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarVisiblesHome().stream().map(this::toResponse).toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('sitio.novedad')")
    public ResponseEntity<ApiResponse<List<NovedadLocalResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarTodas().stream().map(this::toResponse).toList()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sitio.novedad')")
    public ResponseEntity<ApiResponse<NovedadLocalResponse>> crear(@Valid @RequestBody CrearNovedadCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(useCase.crear(command))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sitio.novedad')")
    public ResponseEntity<ApiResponse<NovedadLocalResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarNovedadCommand command) {
        ActualizarNovedadCommand withId = ActualizarNovedadCommand.builder()
                .id(id).titulo(command.getTitulo()).descripcion(command.getDescripcion())
                .imagenUrl(command.getImagenUrl()).textoCta(command.getTextoCta())
                .urlCta(command.getUrlCta()).prioridad(command.getPrioridad())
                .fechaInicio(command.getFechaInicio()).fechaFin(command.getFechaFin())
                .visibleHome(command.isVisibleHome()).destacada(command.isDestacada())
                .activa(command.isActiva()).build();
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.actualizar(withId))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sitio.novedad')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private NovedadLocalResponse toResponse(NovedadLocalQuery q) {
        return NovedadLocalResponse.builder()
                .id(q.getId()).titulo(q.getTitulo()).descripcion(q.getDescripcion())
                .imagenUrl(q.getImagenUrl()).textoCta(q.getTextoCta()).urlCta(q.getUrlCta())
                .prioridad(q.getPrioridad()).fechaInicio(q.getFechaInicio()).fechaFin(q.getFechaFin())
                .visibleHome(q.isVisibleHome()).destacada(q.isDestacada()).activa(q.isActiva())
                .fechaCreacion(q.getFechaCreacion()).fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}
