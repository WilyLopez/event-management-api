package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;
import com.playzone.pems.application.cms.port.in.GestionarContenidoLegalUseCase;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cms/legal")
@RequiredArgsConstructor
public class ContenidoLegalController {

    private final GestionarContenidoLegalUseCase legalUseCase;

    // ── Público ──────────────────────────────────────────────────────────

    @GetMapping("/publico/{tipo}")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(ApiResponse.ok(
                ContenidoLegalResponse.from(legalUseCase.obtenerPorTipo(tipo))));
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ContenidoLegalResponse>>> listar() {
        List<ContenidoLegalResponse> result = legalUseCase.listar()
                .stream().map(ContenidoLegalResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PutMapping("/{idContenidoLegal}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> actualizar(
            @PathVariable Long idContenidoLegal,
            @Valid @RequestBody ActualizarLegalRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        ContenidoLegalResponse response = ContenidoLegalResponse.from(
                legalUseCase.actualizar(new GestionarContenidoLegalUseCase.ActualizarCommand(
                        idContenidoLegal,
                        request.getTitulo(),
                        request.getContenido(),
                        idUsuarioAdmin)));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── Request / Response DTOs ───────────────────────────────────────────

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
        private LocalDateTime fechaActualizacion;

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
