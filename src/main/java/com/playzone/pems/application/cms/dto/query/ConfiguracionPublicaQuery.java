package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.ConfiguracionPublica;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConfiguracionPublicaQuery {

    private Long          id;
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
    private boolean       mantenimientoActivo;
    private String        mensajeMantenimiento;
    private LocalDateTime fechaActualizacion;

    public static ConfiguracionPublicaQuery from(ConfiguracionPublica c) {
        return ConfiguracionPublicaQuery.builder()
                .id(c.getId())
                .nombreNegocio(c.getNombreNegocio())
                .slogan(c.getSlogan())
                .logoUrl(c.getLogoUrl())
                .faviconUrl(c.getFaviconUrl())
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
                .horarioFinDeSemana(c.getHorarioFinDeSemana())
                .copyrightTexto(c.getCopyrightTexto())
                .metaTitle(c.getMetaTitle())
                .metaDescription(c.getMetaDescription())
                .metaKeywords(c.getMetaKeywords())
                .openGraphTitle(c.getOpenGraphTitle())
                .openGraphDescription(c.getOpenGraphDescription())
                .openGraphImageUrl(c.getOpenGraphImageUrl())
                .googleAnalyticsId(c.getGoogleAnalyticsId())
                .metaPixelId(c.getMetaPixelId())
                .colorTema(c.getColorTema())
                .colorSecundario(c.getColorSecundario())
                .mantenimientoActivo(c.isMantenimientoActivo())
                .mensajeMantenimiento(c.getMensajeMantenimiento())
                .fechaActualizacion(c.getFechaActualizacion())
                .build();
    }
}
