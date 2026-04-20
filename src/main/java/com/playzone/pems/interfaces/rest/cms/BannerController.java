package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.port.in.GestionarBannerUseCase;
import com.playzone.pems.shared.response.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BannerController {

    private final GestionarBannerUseCase bannerUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> crear(
            @RequestBody CrearBannerRequest request,
            @RequestAttribute Long idUsuarioAdmin) {

        bannerUseCase.crear(new GestionarBannerUseCase.CrearCommand(
                request.getIdSede(),
                request.getTitulo(),
                request.getDescripcion(),
                request.getImagenUrl(),
                request.getEnlaceDestino(),
                request.getFechaInicio(),
                request.getFechaFin(),
                request.getOrden() != null ? request.getOrden() : 0,
                idUsuarioAdmin));

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.noContent());
    }

    @DeleteMapping("/{idBanner}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long idBanner) {
        bannerUseCase.desactivar(idBanner);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @DeleteMapping("/{idBanner}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long idBanner) {
        bannerUseCase.eliminar(idBanner);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Getter
    @NoArgsConstructor
    public static class CrearBannerRequest {
        private Long      idSede;
        @NotBlank private String    titulo;
        private String    descripcion;
        @NotBlank private String    imagenUrl;
        private String    enlaceDestino;
        @NotNull  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
        private Integer   orden;
    }
}