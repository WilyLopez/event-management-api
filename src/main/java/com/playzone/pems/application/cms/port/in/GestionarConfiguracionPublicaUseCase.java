package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.ConfiguracionPublicaQuery;

public interface GestionarConfiguracionPublicaUseCase {

    record ActualizarCommand(
            String nombreNegocio,
            String slogan,
            String logoUrl,
            String faviconUrl,
            String telefono,
            String telefonoSecundario,
            String whatsapp,
            String correo,
            String correoSecundario,
            String direccion,
            String facebookUrl,
            String instagramUrl,
            String tiktokUrl,
            String youtubeUrl,
            String googleMapsUrl,
            String horarioSemana,
            String horarioFinDeSemana,
            String copyrightTexto,
            String metaTitle,
            String metaDescription,
            String metaKeywords,
            String openGraphTitle,
            String openGraphDescription,
            String openGraphImageUrl,
            String googleAnalyticsId,
            String metaPixelId,
            String colorTema,
            String colorSecundario,
            boolean mantenimientoActivo,
            String mensajeMantenimiento
    ) {}

    ConfiguracionPublicaQuery obtener();

    ConfiguracionPublicaQuery actualizar(ActualizarCommand command);
}
