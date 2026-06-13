package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;
import com.playzone.pems.application.cms.port.in.GestionarContenidoLegalUseCase;
import com.playzone.pems.infrastructure.security.SupabaseAuthFacade;
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

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cms/legal")
@RequiredArgsConstructor
public class ContenidoLegalController {

    private final GestionarContenidoLegalUseCase legalUseCase;
    private final SupabaseAuthFacade             supabaseAuthFacade;

    // ── Público ──────────────────────────────────────────────────────────

    @GetMapping("/publico/{tipo}")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> obtenerPublico(
            @PathVariable String tipo) {
        return ResponseEntity.ok(ApiResponse.ok(
                ContenidoLegalResponse.from(legalUseCase.obtenerPorTipo(tipo))));
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<ApiResponse<List<ContenidoLegalResponse>>> listar() {
        List<ContenidoLegalResponse> result = legalUseCase.listar()
                .stream().map(ContenidoLegalResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> crear(
            @Valid @RequestBody CrearLegalRequest request) {
        ContenidoLegalResponse response = ContenidoLegalResponse.from(
                legalUseCase.crear(new GestionarContenidoLegalUseCase.CrearCommand(
                        request.getTipo(),
                        request.getTitulo(),
                        request.getContenido(),
                        supabaseAuthFacade.usuarioActualId().orElseThrow())));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{tipo}")
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> actualizar(
            @PathVariable String tipo,
            @Valid @RequestBody ActualizarLegalRequest request) {
        ContenidoLegalResponse response = ContenidoLegalResponse.from(
                legalUseCase.actualizar(new GestionarContenidoLegalUseCase.ActualizarCommand(
                        tipo,
                        request.getTitulo(),
                        request.getContenido(),
                        supabaseAuthFacade.usuarioActualId().orElseThrow())));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{tipo}/activar")
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> activar(
            @PathVariable String tipo) {
        return ResponseEntity.ok(ApiResponse.ok(
                ContenidoLegalResponse.from(legalUseCase.activar(tipo))));
    }

    @PatchMapping("/{tipo}/desactivar")
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> desactivar(
            @PathVariable String tipo) {
        return ResponseEntity.ok(ApiResponse.ok(
                ContenidoLegalResponse.from(legalUseCase.desactivar(tipo))));
    }

    @DeleteMapping("/{tipo}")
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<Void> eliminar(@PathVariable String tipo) {
        legalUseCase.eliminar(tipo);
        return ResponseEntity.noContent().build();
    }

    // ── Request / Response DTOs ───────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    public static class CrearLegalRequest {
        @NotBlank private String tipo;
        @NotBlank private String titulo;
                  private String contenido;
    }

    @Getter
    @NoArgsConstructor
    public static class ActualizarLegalRequest {
        @NotBlank private String titulo;
        @NotBlank private String contenido;
    }

    @Getter
    @Builder
    public static class ContenidoLegalResponse {
        private Long          id;
        private String        tipo;
        private String        titulo;
        private String        contenido;
        private int           version;
        private boolean       activo;
        private OffsetDateTime fechaActualizacion;

        public static ContenidoLegalResponse from(ContenidoLegalQuery q) {
            return ContenidoLegalResponse.builder()
                    .id(q.getId())
                    .tipo(q.getTipo())
                    .titulo(q.getTitulo())
                    .contenido(q.getContenido())
                    .version(q.getVersion())
                    .activo(q.isActivo())
                    .fechaActualizacion(q.getFechaActualizacion())
                    .build();
        }
    }
}
