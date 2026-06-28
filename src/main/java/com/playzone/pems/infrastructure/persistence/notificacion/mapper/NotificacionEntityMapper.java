package com.playzone.pems.infrastructure.persistence.notificacion.mapper;

import com.playzone.pems.domain.notificacion.model.Notificacion;
import com.playzone.pems.infrastructure.persistence.notificacion.entity.NotificacionEntity;
import com.playzone.pems.infrastructure.persistence.notificacion.entity.TipoNotificacionEntity;
import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.ClientePerfilEntity;
import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.PerfilUsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificacionEntityMapper {

    public Notificacion toDomain(NotificacionEntity e) {
        if (e == null) return null;
        return Notificacion.builder()
                .id(e.getId())
                .tipoCodigo(e.getTipo().getCodigo())
                .destinatarioUsuarioId(
                        e.getDestinatarioUsuario() != null ? e.getDestinatarioUsuario().getId() : null)
                .destinatarioClienteId(
                        e.getDestinatarioCliente() != null ? e.getDestinatarioCliente().getId() : null)
                .entidadTipo(e.getEntidadTipo())
                .entidadId(e.getEntidadId())
                .titulo(e.getTitulo())
                .mensaje(e.getMensaje())
                .urlAccion(e.getUrlAccion())
                .metadata(e.getMetadata())
                .leida(e.isLeida())
                .leidaAt(e.getFechaLectura())
                .prioridad(e.getPrioridad())
                .expiraAt(e.getFechaExpiracion())
                .createdAt(e.getFechaCreacion())
                .build();
    }

    public NotificacionEntity toEntity(Notificacion d,
                                       TipoNotificacionEntity tipo,
                                       PerfilUsuarioEntity destinatarioUsuario,
                                       ClientePerfilEntity destinatarioCliente) {
        if (d == null) return null;
        return NotificacionEntity.builder()
                .id(d.getId())
                .tipo(tipo)
                .destinatarioUsuario(destinatarioUsuario)
                .destinatarioCliente(destinatarioCliente)
                .entidadTipo(d.getEntidadTipo())
                .entidadId(d.getEntidadId())
                .titulo(d.getTitulo())
                .mensaje(d.getMensaje())
                .urlAccion(d.getUrlAccion())
                .metadata(d.getMetadata() != null ? d.getMetadata() : "{}")
                .leida(d.isLeida())
                .prioridad(d.getPrioridad())
                .fechaExpiracion(d.getExpiraAt())
                .build();
    }
}
