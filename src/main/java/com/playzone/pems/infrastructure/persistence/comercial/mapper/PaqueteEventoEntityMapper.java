package com.playzone.pems.infrastructure.persistence.comercial.mapper;

import com.playzone.pems.domain.comercial.model.PaqueteEvento;
import com.playzone.pems.infrastructure.persistence.comercial.entity.BeneficioPaqueteEntity;
import com.playzone.pems.infrastructure.persistence.comercial.entity.PaqueteEventoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaqueteEventoEntityMapper {

    public PaqueteEvento toDomain(PaqueteEventoEntity entity, List<BeneficioPaqueteEntity> beneficios) {
        return PaqueteEvento.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .slug(entity.getSlug())
                .descripcionCorta(entity.getDescripcionCorta())
                .descripcionLarga(entity.getDescripcionLarga())
                .precio(entity.getPrecio())
                .badge(entity.getBadge())
                .color(entity.getColor())
                .imagenUrl(entity.getImagenUrl())
                .duracionMinutos(entity.getDuracionMinutos())
                .limitepersonas(entity.getLimitepersonas())
                .activo(entity.isActivo())
                .destacado(entity.isDestacado())
                .orden(entity.getOrden())
                .beneficios(beneficios.stream().map(BeneficioPaqueteEntity::getDescripcion).toList())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public PaqueteEventoEntity toEntity(PaqueteEvento domain) {
        return PaqueteEventoEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .slug(domain.getSlug())
                .descripcionCorta(domain.getDescripcionCorta())
                .descripcionLarga(domain.getDescripcionLarga())
                .precio(domain.getPrecio())
                .badge(domain.getBadge())
                .color(domain.getColor())
                .imagenUrl(domain.getImagenUrl())
                .duracionMinutos(domain.getDuracionMinutos())
                .limitepersonas(domain.getLimitepersonas())
                .activo(domain.isActivo())
                .destacado(domain.isDestacado())
                .orden(domain.getOrden())
                .build();
    }
}
