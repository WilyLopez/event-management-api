package com.playzone.pems.infrastructure.persistence.promocion.mapper;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.Promocion;
import com.playzone.pems.domain.promocion.model.PromocionMarketing;
import com.playzone.pems.domain.promocion.model.enums.TipoPromocion;
import com.playzone.pems.infrastructure.persistence.promocion.entity.PromocionEntity;
import com.playzone.pems.infrastructure.persistence.promocion.entity.PromocionMarketingEntity;
import org.springframework.stereotype.Component;

@Component
public class PromocionEntityMapper {

    public Promocion toDomain(PromocionEntity e) {
        if (e == null) return null;
        return Promocion.builder()
                .id(e.getId())
                .tipoPromocion(TipoPromocion.desdeCodigo(e.getTipoCodigo()))
                .idSede(e.getSedeId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .valorDescuento(e.getValorDescuento())
                .minimoPersonas(e.getMinimoPersonas())
                .soloTipoDia(e.getTipoDiaCodigo() != null ? TipoDia.desdeCodigo(e.getTipoDiaCodigo()) : null)
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .activo(e.isEsActivo())
                .esAutomatica(e.isEsAutomatica())
                .idUsuarioCreador(e.getCreatedBy())
                .fechaCreacion(e.getCreatedAt() != null ? e.getCreatedAt() : null)
                .prioridad(e.getPrioridad())
                .limiteUsos(e.getLimiteUsos())
                .limitePorCliente(e.getLimitePorCliente())
                .montoMinimo(e.getMontoMinimo())
                .marketing(toMarketingDomain(e.getMarketing()))
                .build();
    }

    private PromocionMarketing toMarketingDomain(PromocionMarketingEntity m) {
        if (m == null) return null;
        return PromocionMarketing.builder()
                .imagenPath(m.getImagenPath())
                .bannerPath(m.getBannerPath())
                .colorDestacado(m.getColorDestacado())
                .textoPublicitario(m.getTextoPublicitario())
                .textoBoton(m.getTextoBoton())
                .urlBoton(m.getUrlBoton())
                .mostrarEnInicio(m.isMostrarEnInicio())
                .mostrarEnCarrusel(m.isMostrarEnCarrusel())
                .mostrarEnPromociones(m.isMostrarEnPromociones())
                .mostrarEnCheckout(m.isMostrarEnCheckout())
                .soloMovil(m.isSoloMovil())
                .build();
    }

    public PromocionEntity toEntity(Promocion d) {
        if (d == null) return null;
        PromocionEntity entity = PromocionEntity.builder()
                .id(d.getId())
                .tipoCodigo(d.getTipoPromocion().getCodigo())
                .sedeId(d.getIdSede())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .valorDescuento(d.getValorDescuento())
                .tipoDiaCodigo(d.getSoloTipoDia() != null ? d.getSoloTipoDia().getCodigo() : null)
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .esAutomatica(d.isEsAutomatica())
                .esActivo(d.isActivo())
                .prioridad(d.getPrioridad())
                .minimoPersonas(d.getMinimoPersonas())
                .montoMinimo(d.getMontoMinimo())
                .limiteUsos(d.getLimiteUsos())
                .limitePorCliente(d.getLimitePorCliente())
                .createdBy(d.getIdUsuarioCreador())
                .build();

        if (d.getMarketing() != null) {
            entity.setMarketing(toMarketingEntity(d.getMarketing(), entity));
        }
        return entity;
    }

    private PromocionMarketingEntity toMarketingEntity(PromocionMarketing m, PromocionEntity entity) {
        return PromocionMarketingEntity.builder()
                .promocion(entity)
                .imagenPath(m.getImagenPath())
                .bannerPath(m.getBannerPath())
                .colorDestacado(m.getColorDestacado())
                .textoPublicitario(m.getTextoPublicitario())
                .textoBoton(m.getTextoBoton())
                .urlBoton(m.getUrlBoton())
                .mostrarEnInicio(m.isMostrarEnInicio())
                .mostrarEnCarrusel(m.isMostrarEnCarrusel())
                .mostrarEnPromociones(m.isMostrarEnPromociones())
                .mostrarEnCheckout(m.isMostrarEnCheckout())
                .soloMovil(m.isSoloMovil())
                .build();
    }
}
