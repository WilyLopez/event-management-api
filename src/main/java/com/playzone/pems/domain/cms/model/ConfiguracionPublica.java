package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionPublica {

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
}
