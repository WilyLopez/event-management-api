package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.port.in.ModerarResenaUseCase;
import com.playzone.pems.domain.cms.model.Resena;
import com.playzone.pems.interfaces.rest.cms.response.ResenaResponse;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ModerarResenaUseCase moderarUseCase;

    // ── Público ──────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<ResenaResponse>> submit(
            @Valid @RequestBody SubmitResenaRequest request,
            @RequestAttribute(required = false) Long idCliente) {
        Resena resena = moderarUseCase.submit(new ModerarResenaUseCase.SubmitCommand(
                idCliente,
                request.getIdEventoPrivado(),
                request.getNombreAutor(),
                request.getContenido(),
                request.getCalificacion(),
                request.getFotoUrl()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(resena)));
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ResenaResponse>>> listar(
            @RequestParam(defaultValue = "false") boolean pendientes,
            Pageable pageable) {
        var responses = moderarUseCase.listar(pendientes, pageable)
                .getContent().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PostMapping("/{idResena}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ResenaResponse>> aprobar(
            @PathVariable Long idResena,
            @RequestAttribute Long idUsuarioAdmin) {
        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(moderarUseCase.aprobar(idResena, idUsuarioAdmin))));
    }

    @PostMapping("/{idResena}/responder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ResenaResponse>> responder(
            @PathVariable Long idResena,
            @Valid @RequestBody ResponderRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        return ResponseEntity.ok(ApiResponse.ok(
                toResponse(moderarUseCase.responder(new ModerarResenaUseCase.ResponderCommand(
                        idResena, request.getRespuesta(), idUsuarioAdmin)))));
    }

    @PatchMapping("/{idResena}/destacar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> destacar(@PathVariable Long idResena) {
        moderarUseCase.destacar(idResena);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{idResena}/quitar-destacado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> quitarDestacado(@PathVariable Long idResena) {
        moderarUseCase.quitarDestacado(idResena);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{idResena}/home")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleHome(
            @PathVariable Long idResena,
            @RequestParam boolean mostrar) {
        moderarUseCase.toggleMostrarHome(idResena, mostrar);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/{idResena}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rechazar(@PathVariable Long idResena) {
        moderarUseCase.rechazar(idResena);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    // ── Request DTOs ──────────────────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    public static class SubmitResenaRequest {
        private Long   idEventoPrivado;
        @NotBlank      private String nombreAutor;
        @NotBlank      private String contenido;
        @NotNull @Min(1) @Max(5) private Integer calificacion;
        private String fotoUrl;
    }

    @Getter
    @NoArgsConstructor
    public static class ResponderRequest {
        @NotBlank private String respuesta;
    }

    // ── Mapping ───────────────────────────────────────────────────────────

    private ResenaResponse toResponse(Resena r) {
        return ResenaResponse.builder()
                .id(r.getId())
                .nombreAutor(r.getNombreAutor())
                .contenido(r.getContenido())
                .calificacion(r.getCalificacion())
                .aprobada(r.isAprobada())
                .fotoUrl(r.getFotoUrl())
                .respuestaAdmin(r.getRespuestaAdmin())
                .fechaRespuesta(r.getFechaRespuesta())
                .destacada(r.isDestacada())
                .mostrarHome(r.isMostrarHome())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}
