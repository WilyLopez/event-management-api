package com.playzone.pems.interfaces.rest.cms;

import com.playzone.pems.application.cms.dto.query.ConfiguracionPublicaQuery;
import com.playzone.pems.application.cms.port.in.GestionarConfiguracionPublicaUseCase;
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

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/cms/configuracion")
@RequiredArgsConstructor
public class ConfiguracionPublicaController {

    private final GestionarConfiguracionPublicaUseCase configUseCase;

    // ── Público ──────────────────────────────────────────────────────────

    @GetMapping("/publica")
    public ResponseEntity<ApiResponse<ConfiguracionPublicaResponse>> obtenerPublica() {
        return ResponseEntity.ok(ApiResponse.ok(
                ConfiguracionPublicaResponse.from(configUseCase.obtener())));
    }

    // ── Admin ─────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAuthority('configuracion.editar')")
    public ResponseEntity<ApiResponse<ConfiguracionPublicaResponse>> obtener() {
        return ResponseEntity.ok(ApiResponse.ok(
                ConfiguracionPublicaResponse.from(configUseCase.obtener())));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('configuracion.editar')")
    public ResponseEntity<ApiResponse<ConfiguracionPublicaResponse>> actualizar(
            @Valid @RequestBody ActualizarConfiguracionRequest request) {
        // Actualizar la configuración pública del sitio web
        ConfiguracionPublicaResponse response = ConfiguracionPublicaResponse.from(
                configUseCase.actualizar(new GestionarConfiguracionPublicaUseCase.ActualizarCommand(
                        request.getNombreNegocio(),
                        request.getSlogan(),
                        request.getLogoUrl(),
                        request.getFaviconUrl(),
                        request.getTelefono(),
                        request.getTelefonoSecundario(),
                        request.getWhatsapp(),
                        request.getCorreo(),
                        request.getCorreoSecundario(),
                        request.getDireccion(),
                        request.getFacebookUrl(),
                        request.getInstagramUrl(),
                        request.getTiktokUrl(),
                        request.getYoutubeUrl(),
                        request.getGoogleMapsUrl(),
                        request.getHorarioSemana(),
                        request.getHorarioFinDeSemana(),
                        request.getCopyrightTexto(),
                        request.getMetaTitle(),
                        request.getMetaDescription(),
                        request.getMetaKeywords(),
                        request.getOpenGraphTitle(),
                        request.getOpenGraphDescription(),
                        request.getOpenGraphImageUrl(),
                        request.getGoogleAnalyticsId(),
                        request.getMetaPixelId(),
                        request.getColorTema(),
                        request.getColorSecundario(),
                        request.getMetricasNegocio(),
                        request.getReglasLocal(),
                        request.isMantenimientoActivo(),
                        request.getMensajeMantenimiento())));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── Request / Response DTOs ───────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    public static class ActualizarConfiguracionRequest {
        @NotBlank private String  nombreNegocio;
        private String  slogan;
        private String  logoUrl;
        private String  faviconUrl;
        private String  telefono;
        private String  telefonoSecundario;
        private String  whatsapp;
        private String  correo;
        private String  correoSecundario;
        private String  direccion;
        private String  facebookUrl;
        private String  instagramUrl;
        private String  tiktokUrl;
        private String  youtubeUrl;
        private String  googleMapsUrl;
        private String  horarioSemana;
        private String  horarioFinDeSemana;
        private String  copyrightTexto;
        private String  metaTitle;
        private String  metaDescription;
        private String  metaKeywords;
        private String  openGraphTitle;
        private String  openGraphDescription;
        private String  openGraphImageUrl;
        private String  googleAnalyticsId;
        private String  metaPixelId;
        private String  colorTema;
        private String  colorSecundario;
        private String  metricasNegocio;
        private String  reglasLocal;
        private boolean mantenimientoActivo;
        private String  mensajeMantenimiento;
    }

    @Getter
    @Builder
    public static class ConfiguracionPublicaResponse {
        private String        nombreNegocio;
        private String        slogan;
        private String        logoUrl;
        private String        faviconUrl;
        private String        telefono;
        private String        telefonoSecundario;
        private String        whatsapp;
        private String        correo;
        private String        correoSecundario;
        private String        direccion;
        private String        facebookUrl;
        private String        instagramUrl;
        private String        tiktokUrl;
        private String        youtubeUrl;
        private String        googleMapsUrl;
        private String        horarioSemana;
        private String        horarioFinDeSemana;
        private String        copyrightTexto;
        private String        metaTitle;
        private String        metaDescription;
        private String        metaKeywords;
        private String        openGraphTitle;
        private String        openGraphDescription;
        private String        openGraphImageUrl;
        private String        googleAnalyticsId;
        private String        metaPixelId;
        private String        colorTema;
        private String        colorSecundario;
        private String        metricasNegocio;
        private String        reglasLocal;
        private boolean       mantenimientoActivo;
        private String        mensajeMantenimiento;
        private OffsetDateTime updatedAt;

        public static ConfiguracionPublicaResponse from(ConfiguracionPublicaQuery q) {
            return ConfiguracionPublicaResponse.builder()
                    .nombreNegocio(q.getNombreNegocio())
                    .slogan(q.getSlogan())
                    .logoUrl(q.getLogoPath())
                    .faviconUrl(q.getFaviconPath())
                    .telefono(q.getTelefono())
                    .telefonoSecundario(q.getTelefonoSecundario())
                    .whatsapp(q.getWhatsapp())
                    .correo(q.getCorreo())
                    .correoSecundario(q.getCorreoSecundario())
                    .direccion(q.getDireccion())
                    .facebookUrl(q.getFacebookUrl())
                    .instagramUrl(q.getInstagramUrl())
                    .tiktokUrl(q.getTiktokUrl())
                    .youtubeUrl(q.getYoutubeUrl())
                    .googleMapsUrl(q.getGoogleMapsUrl())
                    .horarioSemana(q.getHorarioSemana())
                    .horarioFinDeSemana(q.getHorarioFinSemana())
                    .copyrightTexto(q.getCopyrightTexto())
                    .metaTitle(q.getMetaTitle())
                    .metaDescription(q.getMetaDescription())
                    .metaKeywords(q.getMetaKeywords())
                    .openGraphTitle(q.getOpenGraphTitle())
                    .openGraphDescription(q.getOpenGraphDescription())
                    .openGraphImageUrl(q.getOpenGraphImagePath())
                    .googleAnalyticsId(q.getGoogleAnalyticsId())
                    .metaPixelId(q.getMetaPixelId())
                    .colorTema(q.getColorTema())
                    .colorSecundario(q.getColorSecundario())
                    .metricasNegocio(q.getMetricasNegocio())
                    .reglasLocal(q.getReglasLocal())
                    .mantenimientoActivo(q.isEsMantenimientoActivo())
                    .mensajeMantenimiento(q.getMensajeMantenimiento())
                    .updatedAt(q.getUpdatedAt())
                    .build();
        }
    }
}
