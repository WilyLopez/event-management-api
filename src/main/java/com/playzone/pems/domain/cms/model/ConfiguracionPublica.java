package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionPublica {

    private Long          id;

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

    private boolean       esMantenimientoActivo;
    private String        mensajeMantenimiento;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID          updatedBy;
}
