package com.playzone.pems.infrastructure.persistence.comercial.mapper;

import com.playzone.pems.domain.comercial.model.ActividadLocal;
import com.playzone.pems.infrastructure.persistence.comercial.entity.ActividadLocalEntity;
import com.playzone.pems.infrastructure.persistence.comercial.entity.ZonaJuegoEntity;
import org.springframework.stereotype.Component;

@Component
public class ActividadLocalEntityMapper {

    public ActividadLocal toDomain(ActividadLocalEntity entity) {
        if (entity == null) return null;
        return ActividadLocal.builder()
                .id(entity.getId())
                .idZona(entity.getZona() != null ? entity.getZona().getId() : null)
                .nombreZona(entity.getZona() != null ? entity.getZona().getNombre() : null)
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .imagenUrl(entity.getImagenUrl())
                .activa(entity.isActiva())
                .destacada(entity.isDestacada())
                .esEspecial(entity.isEsEspecial())
                .orden(entity.getOrden())
                .fechaCreacion(entity.getCreatedAt())
                .fechaActualizacion(entity.getUpdatedAt())
                .build();
    }

    public ActividadLocalEntity toEntity(ActividadLocal domain, ZonaJuegoEntity zona) {
        if (domain == null) return null;
        return ActividadLocalEntity.builder()
                .id(domain.getId())
                .zona(zona)
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .imagenUrl(domain.getImagenUrl())
                .activa(domain.isActiva())
                .destacada(domain.isDestacada())
                .esEspecial(domain.isEsEspecial())
                .orden(domain.getOrden())
                .build();
    }
}
