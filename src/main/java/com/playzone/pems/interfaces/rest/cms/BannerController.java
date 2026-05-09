package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.query.BannerQuery;
import com.playzone.pems.application.cms.port.in.GestionarBannerUseCase;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final GestionarBannerUseCase bannerUseCase;

    // ── Público ──────────────────────────────────────────────────────────

    @GetMapping("/publico")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> listarPublicos(
            @RequestParam(required = false) Long idSede) {
        List<BannerResponse> banners = bannerUseCase.listarPublicos(idSede)
                .stream().map(BannerResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(banners));
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<BannerResponse>>> listar(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<BannerResponse> respuesta = PagedResponse.of(
                bannerUseCase.listar(PageRequest.of(page, size, Sort.by("orden").ascending()))
                        .map(BannerResponse::from));
        return ResponseEntity.ok(ApiResponse.ok(respuesta));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponse>> crear(
            @Valid @RequestBody CrearBannerRequest request,
            @RequestAttribute Long idUsuarioAdmin) {
        BannerResponse response = BannerResponse.from(
                bannerUseCase.crear(new GestionarBannerUseCase.CrearCommand(
                        request.getIdSede(),
                        request.getTitulo(),
                        request.getDescripcion(),
                        request.getImagenUrl(),
                        request.getEnlaceDestino(),
                        request.getFechaInicio(),
                        request.getFechaFin(),
                        request.getOrden() != null ? request.getOrden() : 0,
                        idUsuarioAdmin)));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{idBanner}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponse>> actualizar(
            @PathVariable Long idBanner,
            @Valid @RequestBody ActualizarBannerRequest request) {
        BannerResponse response = BannerResponse.from(
                bannerUseCase.actualizar(new GestionarBannerUseCase.ActualizarCommand(
                        idBanner,
                        request.getIdSede(),
                        request.getTitulo(),
                        request.getDescripcion(),
                        request.getImagenUrl(),
                        request.getEnlaceDestino(),
                        request.getFechaInicio(),
                        request.getFechaFin(),
                        request.getOrden() != null ? request.getOrden() : 0)));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{idBanner}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long idBanner) {
        bannerUseCase.activar(idBanner);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{idBanner}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long idBanner) {
        bannerUseCase.desactivar(idBanner);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping("/{idBanner}/duplicar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponse>> duplicar(
            @PathVariable Long idBanner,
            @RequestAttribute Long idUsuarioAdmin) {
        BannerResponse response = BannerResponse.from(
                bannerUseCase.duplicar(idBanner, idUsuarioAdmin));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/reordenar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> reordenar(
            @RequestBody ReordenarRequest request) {
        bannerUseCase.reordenar(new GestionarBannerUseCase.ReordenarCommand(request.getIds()));
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/{idBanner}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long idBanner) {
        bannerUseCase.eliminar(idBanner);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    // ── Request / Response DTOs ───────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    public static class CrearBannerRequest {
        private Long      idSede;
        @NotBlank  private String    titulo;
        private String    descripcion;
        @NotBlank  private String    imagenUrl;
        private String    enlaceDestino;
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate fechaInicio;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate fechaFin;
        private Integer   orden;
    }

    @Getter
    @NoArgsConstructor
    public static class ActualizarBannerRequest {
        private Long      idSede;
        @NotBlank  private String    titulo;
        private String    descripcion;
        @NotBlank  private String    imagenUrl;
        private String    enlaceDestino;
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate fechaInicio;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate fechaFin;
        private Integer   orden;
    }

    @Getter
    @NoArgsConstructor
    public static class ReordenarRequest {
        @NotNull private List<Long> ids;
    }

    @Getter
    @Builder
    public static class BannerResponse {
        private Long          id;
        private Long          idSede;
        private String        titulo;
        private String        descripcion;
        private String        imagenUrl;
        private String        enlaceDestino;
        private LocalDate     fechaInicio;
        private LocalDate     fechaFin;
        private boolean       activo;
        private int           orden;
        private LocalDateTime fechaCreacion;

        public static BannerResponse from(BannerQuery q) {
            return BannerResponse.builder()
                    .id(q.getId())
                    .idSede(q.getIdSede())
                    .titulo(q.getTitulo())
                    .descripcion(q.getDescripcion())
                    .imagenUrl(q.getImagenUrl())
                    .enlaceDestino(q.getEnlaceDestino())
                    .fechaInicio(q.getFechaInicio())
                    .fechaFin(q.getFechaFin())
                    .activo(q.isActivo())
                    .orden(q.getOrden())
                    .fechaCreacion(q.getFechaCreacion())
                    .build();
        }
    }
}
