package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.dto.query.ConfiguracionPublicaQuery;
import com.playzone.pems.application.cms.port.in.GestionarConfiguracionPublicaUseCase;
import com.playzone.pems.domain.cms.model.ConfiguracionPublica;
import com.playzone.pems.domain.cms.repository.ConfiguracionPublicaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfiguracionPublicaService implements GestionarConfiguracionPublicaUseCase {

    private final ConfiguracionPublicaRepository configRepository;

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionPublicaQuery obtener() {
        return ConfiguracionPublicaQuery.from(
                configRepository.findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("ConfiguracionPublica", 1L)));
    }

    @Override
    @Transactional
    public ConfiguracionPublicaQuery actualizar(ActualizarCommand cmd) {
        ConfiguracionPublica existente = configRepository.findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ConfiguracionPublica", 1L));

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
}
