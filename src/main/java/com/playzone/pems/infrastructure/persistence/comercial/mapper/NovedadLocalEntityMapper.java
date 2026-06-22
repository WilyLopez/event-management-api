package com.playzone.pems.infrastructure.persistence.comercial.mapper;

import com.playzone.pems.domain.comercial.model.NovedadLocal;
import com.playzone.pems.infrastructure.persistence.comercial.entity.NovedadLocalEntity;
import org.springframework.stereotype.Component;

@Component
public class NovedadLocalEntityMapper {

    public NovedadLocal toDomain(NovedadLocalEntity entity) {
        if (entity == null) return null;
        return NovedadLocal.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .descripcion(entity.getContenido())
                .imagenUrl(entity.getImagenUrl())
                .textoCta(entity.getTextoCta())
                .urlCta(entity.getUrlCta())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .activa(entity.isActiva())
                .visibleHome(entity.isVisibleHome())
                .destacada(entity.isDestacada())
                .prioridad(entity.getPrioridad())
                .fechaCreacion(entity.getCreatedAt())
                .fechaActualizacion(entity.getUpdatedAt())
                .build();
    }

    public NovedadLocalEntity toEntity(NovedadLocal domain) {
        if (domain == null) return null;
        return NovedadLocalEntity.builder()
                .id(domain.getId())
                .titulo(domain.getTitulo())
                .contenido(domain.getDescripcion())
                .imagenUrl(domain.getImagenUrl())
                .textoCta(domain.getTextoCta())
                .urlCta(domain.getUrlCta())
                .fechaInicio(domain.getFechaInicio())
                .fechaFin(domain.getFechaFin())
                .activa(domain.isActiva())
                .visibleHome(domain.isVisibleHome())
                .destacada(domain.isDestacada())
                .prioridad(domain.getPrioridad())
                .build();
    }
}
