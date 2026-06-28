package com.playzone.pems.infrastructure.persistence.notificacion.mapper;

import com.playzone.pems.domain.notificacion.model.NotificacionEntrega;
import com.playzone.pems.infrastructure.persistence.notificacion.entity.NotificacionEntregaEntity;
import com.playzone.pems.infrastructure.persistence.notificacion.entity.NotificacionEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificacionEntregaEntityMapper {

    public NotificacionEntrega toDomain(NotificacionEntregaEntity e) {
        if (e == null) return null;
        return NotificacionEntrega.builder()
                .id(e.getId())
                .notificacionId(e.getNotificacion().getId())
                .canal(e.getCanal())
                .estado(e.getEstado())
                .intentos(e.getIntentos())
                .enviadoAt(e.getFechaEnvio())
                .mensajeError(e.getMensajeError())
                .proveedorId(e.getProveedorId())
                .metadata(e.getMetadata())
                .createdAt(e.getFechaCreacion())
                .build();
    }

    public NotificacionEntregaEntity toEntity(NotificacionEntrega d, NotificacionEntity notificacion) {
        if (d == null) return null;
        return NotificacionEntregaEntity.builder()
                .id(d.getId())
                .notificacion(notificacion)
                .canal(d.getCanal())
                .estado(d.getEstado())
                .intentos(d.getIntentos())
                .fechaEnvio(d.getEnviadoAt())
                .mensajeError(d.getMensajeError())
                .proveedorId(d.getProveedorId())
                .metadata(d.getMetadata() != null ? d.getMetadata() : "{}")
                .build();
    }
}
