package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.query.FaqQuery;
import com.playzone.pems.application.cms.port.in.GestionarFaqUseCase;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.response.PagedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cms/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final GestionarFaqUseCase faqUseCase;

    // ── Público ──────────────────────────────────────────────────────────

    @GetMapping("/publico")
    public ResponseEntity<ApiResponse<List<FaqResponse>>> listarPublico() {
        List<FaqResponse> result = faqUseCase.listarVisibles()
                .stream().map(FaqResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<FaqResponse>>> listar(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<FaqResponse> respuesta = PagedResponse.of(
                faqUseCase.listar(PageRequest.of(page, size, Sort.by("ordenVisualizacion").ascending()))
                        .map(FaqResponse::from));
        return ResponseEntity.ok(ApiResponse.ok(respuesta));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FaqResponse>> crear(
            @Valid @RequestBody CrearFaqRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        FaqResponse response = FaqResponse.from(
                faqUseCase.crear(new GestionarFaqUseCase.CrearCommand(
                        request.getPregunta(),
                        request.getRespuesta(),
                        request.getOrden() != null ? request.getOrden() : 0,
                        idUsuarioAdmin)));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{idFaq}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FaqResponse>> actualizar(
            @PathVariable Long idFaq,
            @Valid @RequestBody ActualizarFaqRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        FaqResponse response = FaqResponse.from(
                faqUseCase.actualizar(new GestionarFaqUseCase.ActualizarCommand(
                        idFaq,
                        request.getPregunta(),
                        request.getRespuesta(),
                        request.getOrden() != null ? request.getOrden() : 0,
                        idUsuarioAdmin)));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{idFaq}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long idFaq) {
        faqUseCase.activar(idFaq);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{idFaq}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long idFaq) {
        faqUseCase.desactivar(idFaq);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PutMapping("/reordenar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> reordenar(@RequestBody ReordenarFaqRequest request) {
        faqUseCase.reordenar(new GestionarFaqUseCase.ReordenarCommand(request.getIds()));
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/{idFaq}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long idFaq) {
        faqUseCase.eliminar(idFaq);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    // ── Request / Response DTOs ───────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    public static class CrearFaqRequest {
        @NotBlank private String  pregunta;
        @NotBlank private String  respuesta;
        private           Integer orden;
    }

    @Getter
    @NoArgsConstructor
    public static class ActualizarFaqRequest {
        @NotBlank private String  pregunta;
        @NotBlank private String  respuesta;
        private           Integer orden;
    }

    @Getter
    @NoArgsConstructor
    public static class ReordenarFaqRequest {
        @NotNull private List<Long> ids;
    }

    @Getter
    @Builder
    public static class FaqResponse {
        private Long          id;
        private String        pregunta;
        private String        respuesta;
        private int           ordenVisualizacion;
        private boolean       visible;
        private LocalDateTime fechaActualizacion;

        public static FaqResponse from(FaqQuery q) {
            return FaqResponse.builder()
                    .id(q.getId())
                    .pregunta(q.getPregunta())
                    .respuesta(q.getRespuesta())
                    .ordenVisualizacion(q.getOrdenVisualizacion())
                    .visible(q.isVisible())
                    .fechaActualizacion(q.getFechaActualizacion())
                    .build();
        }
    }
}
