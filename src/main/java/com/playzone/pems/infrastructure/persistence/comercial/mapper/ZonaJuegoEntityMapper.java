package com.playzone.pems.infrastructure.persistence.comercial.mapper;

import com.playzone.pems.domain.comercial.model.ZonaJuego;
import com.playzone.pems.infrastructure.persistence.comercial.entity.MedioZonaJuegoEntity;
import com.playzone.pems.infrastructure.persistence.comercial.entity.ZonaJuegoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZonaJuegoEntityMapper {

    public ZonaJuego toDomain(ZonaJuegoEntity entity, List<MedioZonaJuegoEntity> medios) {
        List<String> imagenes = medios.stream()
                .filter(m -> "IMAGEN".equals(m.getTipo()))
                .map(MedioZonaJuegoEntity::getUrl)
                .toList();
        List<String> videos = medios.stream()
                .filter(m -> "VIDEO".equals(m.getTipo()))
                .map(MedioZonaJuegoEntity::getUrl)
                .toList();
        return ZonaJuego.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .slug(entity.getSlug())
                .descripcion(entity.getDescripcion())
                .edadMinima(entity.getEdadMinima())
                .edadMaxima(entity.getEdadMaxima())
                .activa(entity.isActiva())
                .destacada(entity.isDestacada())
                .orden(entity.getOrden())
                .imagenes(imagenes)
                .videos(videos)
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public ZonaJuegoEntity toEntity(ZonaJuego domain) {
        return ZonaJuegoEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .slug(domain.getSlug())
                .descripcion(domain.getDescripcion())
                .edadMinima(domain.getEdadMinima())
                .edadMaxima(domain.getEdadMaxima())
                .activa(domain.isActiva())
                .destacada(domain.isDestacada())
                .orden(domain.getOrden())
                .build();
    }
}
