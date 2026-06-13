package com.playzone.pems.infrastructure.persistence.comercial.mapper;

import com.playzone.pems.domain.comercial.model.NovedadLocal;
import com.playzone.pems.infrastructure.persistence.comercial.entity.NovedadLocalEntity;
import org.springframework.stereotype.Component;

@Component
public class NovedadLocalEntityMapper {

    public NovedadLocal toDomain(NovedadLocalEntity entity) {
        return NovedadLocal.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .descripcion(entity.getDescripcion())
                .imagenUrl(entity.getImagenUrl())
                .textoCta(entity.getTextoCta())
                .urlCta(entity.getUrlCta())
                .prioridad(entity.getPrioridad())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .visibleHome(entity.isVisibleHome())
                .destacada(entity.isDestacada())
                .activa(entity.isActiva())
                .fechaCreacion(entity.getFechaCreacion() != null ? entity.getFechaCreacion() : null)
                .fechaActualizacion(entity.getFechaActualizacion() != null ? entity.getFechaActualizacion() : null)
                .build();
    }

    public NovedadLocalEntity toEntity(NovedadLocal domain) {
        return NovedadLocalEntity.builder()
                .id(domain.getId())
                .titulo(domain.getTitulo())
                .descripcion(domain.getDescripcion())
                .imagenUrl(domain.getImagenUrl())
                .textoCta(domain.getTextoCta())
                .urlCta(domain.getUrlCta())
                .prioridad(domain.getPrioridad())
                .fechaInicio(domain.getFechaInicio())
                .fechaFin(domain.getFechaFin())
                .visibleHome(domain.isVisibleHome())
                .destacada(domain.isDestacada())
                .activa(domain.isActiva())
                .build();
    }
}
