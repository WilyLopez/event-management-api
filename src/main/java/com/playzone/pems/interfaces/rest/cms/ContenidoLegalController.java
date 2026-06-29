package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.query.ContenidoLegalHistorialQuery;
import com.playzone.pems.application.cms.dto.query.ContenidoLegalQuery;
import com.playzone.pems.application.cms.dto.query.ContenidoLegalResumenQuery;
import com.playzone.pems.application.cms.dto.query.TipoLegalQuery;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cms/legal")
@RequiredArgsConstructor
public class ContenidoLegalController {

    private final GestionarContenidoLegalUseCase legalUseCase;
    private final SupabaseAuthFacade             supabaseAuthFacade;

    // ── Público ──────────────────────────────────────────────────────────

    @GetMapping("/publico")
    public ResponseEntity<ApiResponse<List<LegalResumenResponse>>> listarPublico() {
        List<LegalResumenResponse> result = legalUseCase.listarPublico()
                .stream().map(LegalResumenResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/publico/slug/{slug}")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> obtenerPublicoPorSlug(
            @PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(
                ContenidoLegalResponse.from(legalUseCase.obtenerPublicoPorSlug(slug))));
    }

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

    @GetMapping("/tipos")
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<ApiResponse<List<TipoLegalResponse>>> listarTipos() {
        List<TipoLegalResponse> result = legalUseCase.listarTipos()
                .stream().map(TipoLegalResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{tipo}/historial")
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<ApiResponse<List<LegalHistorialResponse>>> listarHistorial(
            @PathVariable String tipo) {
        List<LegalHistorialResponse> result = legalUseCase.listarHistorial(tipo)
                .stream().map(LegalHistorialResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{tipo}")
    @PreAuthorize("hasAuthority('sitio.legal')")
    public ResponseEntity<ApiResponse<ContenidoLegalResponse>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(ApiResponse.ok(
                ContenidoLegalResponse.from(legalUseCase.obtenerPorTipo(tipo))));
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
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
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

    @Getter
    @Builder
    public static class LegalResumenResponse {
        private String        tipo;
        private String        etiqueta;
        private String        slug;
        private String        titulo;
        private int           version;
        private boolean       visibleFooter;
        private OffsetDateTime fechaActualizacion;

        public static LegalResumenResponse from(ContenidoLegalResumenQuery q) {
            return LegalResumenResponse.builder()
                    .tipo(q.getTipo())
                    .etiqueta(q.getEtiqueta())
                    .slug(q.getSlug())
                    .titulo(q.getTitulo())
                    .version(q.getVersion())
                    .visibleFooter(q.isVisibleFooter())
                    .fechaActualizacion(q.getFechaActualizacion())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class TipoLegalResponse {
        private String  codigo;
        private String  etiqueta;
        private String  slug;
        private int     orden;
        private boolean esSistema;
        private boolean requerido;
        private boolean visibleFooter;
        private boolean yaCreado;

        public static TipoLegalResponse from(TipoLegalQuery q) {
            return TipoLegalResponse.builder()
                    .codigo(q.getCodigo())
                    .etiqueta(q.getEtiqueta())
                    .slug(q.getSlug())
                    .orden(q.getOrden())
                    .esSistema(q.isEsSistema())
                    .requerido(q.isRequerido())
                    .visibleFooter(q.isVisibleFooter())
                    .yaCreado(q.isYaCreado())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class LegalHistorialResponse {
        private Long          id;
        private String        tipo;
        private String        titulo;
        private String        contenido;
        private int           version;
        private UUID          createdBy;
        private OffsetDateTime fechaCreacion;

        public static LegalHistorialResponse from(ContenidoLegalHistorialQuery q) {
            return LegalHistorialResponse.builder()
                    .id(q.getId())
                    .tipo(q.getTipo())
                    .titulo(q.getTitulo())
                    .contenido(q.getContenido())
                    .version(q.getVersion())
                    .createdBy(q.getCreatedBy())
                    .fechaCreacion(q.getFechaCreacion())
                    .build();
        }
    }
}
