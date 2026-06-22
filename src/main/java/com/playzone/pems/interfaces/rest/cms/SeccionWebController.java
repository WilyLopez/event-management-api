package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.query.SeccionWebQuery;
import com.playzone.pems.application.cms.port.in.GestionarSeccionWebUseCase;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cms/secciones")
@RequiredArgsConstructor
public class SeccionWebController {

    private final GestionarSeccionWebUseCase seccionUseCase;

    // ── Público ──────────────────────────────────────────────────────────

    @GetMapping("/publico")
    public ResponseEntity<ApiResponse<List<SeccionWebResponse>>> listarActivas() {
        List<SeccionWebResponse> result = seccionUseCase.listarActivas()
                .stream().map(SeccionWebResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAuthority('sitio.contenido')")
    public ResponseEntity<ApiResponse<List<SeccionWebResponse>>> listar() {
        List<SeccionWebResponse> result = seccionUseCase.listar()
                .stream().map(SeccionWebResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sitio.contenido')")
    public ResponseEntity<ApiResponse<SeccionWebResponse>> crear(
            @Valid @RequestBody CrearSeccionRequest request) {
        SeccionWebResponse response = SeccionWebResponse.from(
                seccionUseCase.crear(new GestionarSeccionWebUseCase.CrearCommand(
                        request.getCodigo(),
                        request.getNombre(),
                        request.getDescripcion(),
                        request.getOrden() != null ? request.getOrden() : 0)));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{codigoSeccion}")
    @PreAuthorize("hasAuthority('sitio.contenido')")
    public ResponseEntity<ApiResponse<SeccionWebResponse>> actualizar(
            @PathVariable String codigoSeccion,
            @Valid @RequestBody ActualizarSeccionRequest request) {
        SeccionWebResponse response = SeccionWebResponse.from(
                seccionUseCase.actualizar(new GestionarSeccionWebUseCase.ActualizarCommand(
                        codigoSeccion,
                        request.getNombre(),
                        request.getDescripcion(),
                        request.getOrden() != null ? request.getOrden() : 0)));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{codigoSeccion}/activar")
    @PreAuthorize("hasAuthority('sitio.contenido')")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable String codigoSeccion) {
        seccionUseCase.activar(codigoSeccion);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{codigoSeccion}/desactivar")
    @PreAuthorize("hasAuthority('sitio.contenido')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable String codigoSeccion) {
        seccionUseCase.desactivar(codigoSeccion);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/{codigoSeccion}")
    @PreAuthorize("hasAuthority('sitio.contenido')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String codigoSeccion) {
        seccionUseCase.eliminar(codigoSeccion);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    // ── Request / Response DTOs ───────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    public static class CrearSeccionRequest {
        @NotBlank private String  codigo;
        @NotBlank private String  nombre;
        private           String  descripcion;
        private           Integer orden;
    }

    @Getter
    @NoArgsConstructor
    public static class ActualizarSeccionRequest {
        @NotBlank private String  nombre;
        private           String  descripcion;
        private           Integer orden;
    }

    @Getter
    @Builder
    public static class SeccionWebResponse {
        private String  codigo;
        private String  nombre;
        private String  descripcion;
        private int     ordenVisualizacion;
        private boolean visible;
        private boolean esSistema;

        public static SeccionWebResponse from(SeccionWebQuery q) {
            return SeccionWebResponse.builder()
                    .codigo(q.getCodigo())
                    .nombre(q.getNombre())
                    .descripcion(q.getDescripcion())
                    .ordenVisualizacion(q.getOrden())
                    .visible(q.isActivo())
                    .esSistema(q.isEsSistema())
                    .build();
        }
    }
}
