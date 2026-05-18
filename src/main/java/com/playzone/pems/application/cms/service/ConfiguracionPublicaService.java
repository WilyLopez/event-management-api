package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.query.ConfiguracionPublicaQuery;
import com.playzone.pems.application.cms.port.in.GestionarConfiguracionPublicaUseCase;
import com.playzone.pems.domain.cms.model.ConfiguracionPublica;
import com.playzone.pems.domain.cms.repository.ConfiguracionPublicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfiguracionPublicaService implements GestionarConfiguracionPublicaUseCase {

    private final ConfiguracionPublicaRepository configRepository;

    @Override
    @Transactional
    public ConfiguracionPublicaQuery obtener() {
        return ConfiguracionPublicaQuery.from(obtenerOAutocrear());
    }

    @Override
    @Transactional
    public ConfiguracionPublicaQuery actualizar(ActualizarCommand cmd) {
        ConfiguracionPublica existente = obtenerOAutocrear();

        ConfiguracionPublica actualizado = existente.toBuilder()
                .nombreNegocio(cmd.nombreNegocio())
                .slogan(cmd.slogan())
                .logoUrl(cmd.logoUrl())
                .faviconUrl(cmd.faviconUrl())
                .telefono(cmd.telefono())
                .telefonoSecundario(cmd.telefonoSecundario())
                .whatsapp(cmd.whatsapp())
                .correo(cmd.correo())
                .correoSecundario(cmd.correoSecundario())
                .direccion(cmd.direccion())
                .facebookUrl(cmd.facebookUrl())
                .instagramUrl(cmd.instagramUrl())
                .tiktokUrl(cmd.tiktokUrl())
                .youtubeUrl(cmd.youtubeUrl())
                .googleMapsUrl(cmd.googleMapsUrl())
                .horarioSemana(cmd.horarioSemana())
                .horarioFinDeSemana(cmd.horarioFinDeSemana())
                .copyrightTexto(cmd.copyrightTexto())
                .metaTitle(cmd.metaTitle())
                .metaDescription(cmd.metaDescription())
                .metaKeywords(cmd.metaKeywords())
                .openGraphTitle(cmd.openGraphTitle())
                .openGraphDescription(cmd.openGraphDescription())
                .openGraphImageUrl(cmd.openGraphImageUrl())
                .googleAnalyticsId(cmd.googleAnalyticsId())
                .metaPixelId(cmd.metaPixelId())
                .colorTema(cmd.colorTema())
                .colorSecundario(cmd.colorSecundario())
                .mantenimientoActivo(cmd.mantenimientoActivo())
                .mensajeMantenimiento(cmd.mensajeMantenimiento())
                .build();

        return ConfiguracionPublicaQuery.from(configRepository.save(actualizado));
    }

    private ConfiguracionPublica obtenerOAutocrear() {
        return configRepository.findFirst()
                .orElseGet(() -> configRepository.save(ConfiguracionPublica.builder()
                        .nombreNegocio("Mi Negocio")
                        .slogan("Slogan por defecto")
                        .mantenimientoActivo(false)
                        .mensajeMantenimiento("Estamos en mantenimiento, por favor regrese más tarde.")
                        .colorTema("#000000")
                        .colorSecundario("#FFFFFF")
                        .build()));
    }
}
