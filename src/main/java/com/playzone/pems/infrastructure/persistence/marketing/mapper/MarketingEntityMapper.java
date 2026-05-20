package com.playzone.pems.infrastructure.persistence.marketing.mapper;

import com.playzone.pems.domain.marketing.model.CampanaEmail;
import com.playzone.pems.domain.marketing.model.EnvioEmail;
import com.playzone.pems.domain.marketing.model.PlantillaEmail;
import com.playzone.pems.domain.marketing.model.TipoEmail;
import com.playzone.pems.infrastructure.persistence.marketing.entity.CampanaEmailEntity;
import com.playzone.pems.infrastructure.persistence.marketing.entity.EnvioEmailEntity;
import com.playzone.pems.infrastructure.persistence.marketing.entity.PlantillaEmailEntity;
import com.playzone.pems.infrastructure.persistence.marketing.entity.TipoEmailEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketingEntityMapper {

    public TipoEmail toDomain(TipoEmailEntity e) {
        return TipoEmail.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .activo(e.isActivo())
                .build();
    }

    public PlantillaEmail toDomain(PlantillaEmailEntity e) {
        return PlantillaEmail.builder()
                .id(e.getId())
                .idTipoEmail(e.getTipoEmail().getId())
                .tipoEmailCodigo(e.getTipoEmail().getCodigo())
                .tipoEmailNombre(e.getTipoEmail().getNombre())
                .nombre(e.getNombre())
                .asunto(e.getAsunto())
                .contenidoHtml(e.getContenidoHtml())
                .contenidoFallback(e.getContenidoFallback())
                .variablesPermitidas(e.getVariablesPermitidas())
                .activa(e.isActiva())
                .idUsuarioEditor(e.getIdUsuarioEditor())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public CampanaEmail toDomain(CampanaEmailEntity e) {
        return CampanaEmail.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .idPlantillaEmail(e.getPlantillaEmail().getId())
                .plantillaNombre(e.getPlantillaEmail().getNombre())
                .estado(e.getEstado())
                .fechaProgramada(e.getFechaProgramada())
                .totalDestinatarios(e.getTotalDestinatarios())
                .totalEnviados(e.getTotalEnviados())
                .totalFallidos(e.getTotalFallidos())
                .idUsuarioCreador(e.getIdUsuarioCreador())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public EnvioEmail toDomain(EnvioEmailEntity e) {
        return EnvioEmail.builder()
                .id(e.getId())
                .idCampanaEmail(e.getIdCampanaEmail())
                .idCliente(e.getIdCliente())
                .destinatario(e.getDestinatario())
                .asunto(e.getAsunto())
                .estado(e.getEstado())
                .intentos(e.getIntentos())
                .fechaEnvio(e.getFechaEnvio())
                .mensajeError(e.getMensajeError())
                .proveedorMensajeId(e.getProveedorMensajeId())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public EnvioEmailEntity toEntity(EnvioEmail d) {
        return EnvioEmailEntity.builder()
                .id(d.getId())
                .idCampanaEmail(d.getIdCampanaEmail())
                .idCliente(d.getIdCliente())
                .destinatario(d.getDestinatario())
                .asunto(d.getAsunto())
                .estado(d.getEstado())
                .intentos(d.getIntentos())
                .fechaEnvio(d.getFechaEnvio())
                .mensajeError(d.getMensajeError())
                .proveedorMensajeId(d.getProveedorMensajeId())
                .build();
    }
}
