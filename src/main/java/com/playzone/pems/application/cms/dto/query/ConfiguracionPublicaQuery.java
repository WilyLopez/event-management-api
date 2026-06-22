package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.ConfiguracionPublica;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ConfiguracionPublicaQuery {

    private String        nombreNegocio;
    private String        slogan;
    private String        logoPath;
    private String        faviconPath;
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
    private String        horarioFinSemana;
    private String        copyrightTexto;
    private String        metaTitle;
    private String        metaDescription;
    private String        metaKeywords;
    private String        openGraphTitle;
    private String        openGraphDescription;
    private String        openGraphImagePath;
    private String        googleAnalyticsId;
    private String        metaPixelId;
    private String        colorTema;
    private String        colorSecundario;
    private String        metricasNegocio;
    private String        reglasLocal;
    private boolean       esMantenimientoActivo;
    private String        mensajeMantenimiento;
    private OffsetDateTime updatedAt;

    public static ConfiguracionPublicaQuery from(ConfiguracionPublica c) {
        return ConfiguracionPublicaQuery.builder()
                .nombreNegocio(c.getNombreNegocio())
                .slogan(c.getSlogan())
                .logoPath(c.getLogoPath())
                .faviconPath(c.getFaviconPath())
                .telefono(c.getTelefono())
                .telefonoSecundario(c.getTelefonoSecundario())
                .whatsapp(c.getWhatsapp())
                .correo(c.getCorreo())
                .correoSecundario(c.getCorreoSecundario())
                .direccion(c.getDireccion())
                .facebookUrl(c.getFacebookUrl())
                .instagramUrl(c.getInstagramUrl())
                .tiktokUrl(c.getTiktokUrl())
                .youtubeUrl(c.getYoutubeUrl())
                .googleMapsUrl(c.getGoogleMapsUrl())
                .horarioSemana(c.getHorarioSemana())
                .horarioFinSemana(c.getHorarioFinSemana())
                .copyrightTexto(c.getCopyrightTexto())
                .metaTitle(c.getMetaTitle())
                .metaDescription(c.getMetaDescription())
                .metaKeywords(c.getMetaKeywords())
                .openGraphTitle(c.getOpenGraphTitle())
                .openGraphDescription(c.getOpenGraphDescription())
                .openGraphImagePath(c.getOpenGraphImagePath())
                .googleAnalyticsId(c.getGoogleAnalyticsId())
                .metaPixelId(c.getMetaPixelId())
                .colorTema(c.getColorTema())
                .colorSecundario(c.getColorSecundario())
                .metricasNegocio(c.getMetricasNegocio())
                .reglasLocal(c.getReglasLocal())
                .esMantenimientoActivo(c.isEsMantenimientoActivo())
                .mensajeMantenimiento(c.getMensajeMantenimiento())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
