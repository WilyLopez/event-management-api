package com.playzone.pems.infrastructure.persistence.comercial.mapper;

import com.playzone.pems.domain.comercial.model.ActividadLocal;
import com.playzone.pems.infrastructure.persistence.comercial.entity.ActividadLocalEntity;
import com.playzone.pems.infrastructure.persistence.comercial.entity.ZonaJuegoEntity;
import org.springframework.stereotype.Component;

@Component
public class ActividadLocalEntityMapper {

    public ActividadLocal toDomain(ActividadLocalEntity entity) {
        return ActividadLocal.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .imagenUrl(entity.getImagenUrl())
                .idZona(entity.getZona() != null ? entity.getZona().getId() : null)
                .nombreZona(entity.getZona() != null ? entity.getZona().getNombre() : null)
                .esEspecial(entity.isEsEspecial())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .activa(entity.isActiva())
                .destacada(entity.isDestacada())
                .orden(entity.getOrden())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public ActividadLocalEntity toEntity(ActividadLocal domain, ZonaJuegoEntity zona) {
        return ActividadLocalEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .imagenUrl(domain.getImagenUrl())
                .zona(zona)
                .esEspecial(domain.isEsEspecial())
                .fechaInicio(domain.getFechaInicio())
                .fechaFin(domain.getFechaFin())
                .activa(domain.isActiva())
                .destacada(domain.isDestacada())
                .orden(domain.getOrden())
                .build();
    }
}
