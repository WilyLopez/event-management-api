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
                .colorHex(entity.getColorHex())
                .imagenPath(entity.getImagenPath())
                .duracionMinutos(entity.getDuracionMinutos())
                .limitePersonas(entity.getLimitePersonas())
                .esActivo(entity.isEsActivo())
                .esDestacado(entity.isEsDestacado())
                .orden(entity.getOrden())
                .tipoEventoCodigo(entity.getTipoEventoCodigo())
                .beneficios(beneficios.stream().map(BeneficioPaqueteEntity::getDescripcion).toList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
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
                .colorHex(domain.getColorHex())
                .imagenPath(domain.getImagenPath())
                .duracionMinutos(domain.getDuracionMinutos())
                .limitePersonas(domain.getLimitePersonas())
                .esActivo(domain.isEsActivo())
                .esDestacado(domain.isEsDestacado())
                .orden(domain.getOrden())
                .tipoEventoCodigo(domain.getTipoEventoCodigo())
                .build();
    }
}
