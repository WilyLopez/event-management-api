package com.playzone.pems.infrastructure.persistence.promocion.mapper;

import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.infrastructure.persistence.promocion.entity.PromocionEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class PromocionEntityMapper {

    public Promocion toDomain(PromocionEntity e) {
        if (e == null) return null;
        return Promocion.builder()
                .id(e.getId())
                .tipoPromocion(e.getTipoPromocion())
                .idSede(e.getSede() != null ? e.getSede().getId() : null)
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .valorDescuento(e.getValorDescuento())
                .condicion(e.getCondicion())
                .minimoPersonas(e.getMinimoPersonas())
                .soloTipoDia(e.getSoloTipoDia())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .activo(e.isActivo())
                .esAutomatica(e.isEsAutomatica())
                .idUsuarioCreador(e.getUsuarioCreador().getId())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public PromocionEntity toEntity(Promocion d, SedeEntity sede, UsuarioAdminEntity creador) {
        if (d == null) return null;
        return PromocionEntity.builder()
                .id(d.getId())
                .tipoPromocion(d.getTipoPromocion())
                .sede(sede)
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .valorDescuento(d.getValorDescuento())
                .condicion(d.getCondicion())
                .minimoPersonas(d.getMinimoPersonas())
                .soloTipoDia(d.getSoloTipoDia())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .activo(d.isActivo())
                .esAutomatica(d.isEsAutomatica())
                .usuarioCreador(creador)
                .build();
    }
}