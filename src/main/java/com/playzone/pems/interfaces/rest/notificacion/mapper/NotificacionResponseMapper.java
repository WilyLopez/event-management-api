package com.playzone.pems.interfaces.rest.notificacion.mapper;

import com.playzone.pems.application.notificacion.dto.query.NotificacionQuery;
import com.playzone.pems.interfaces.rest.notificacion.response.NotificacionResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificacionResponseMapper {

    public NotificacionResponse toResponse(NotificacionQuery q) {
        return NotificacionResponse.builder()
                .id(q.getId())
                .tipoCodigo(q.getTipoCodigo())
                .titulo(q.getTitulo())
                .mensaje(q.getMensaje())
                .prioridad(q.getPrioridad())
                .urlAccion(q.getUrlAccion())
                .leida(q.isLeida())
                .leidaAt(q.getLeidaAt())
                .expiraAt(q.getExpiraAt())
                .createdAt(q.getCreatedAt())
                .entidadTipo(q.getEntidadTipo())
                .entidadId(q.getEntidadId())
                .build();
    }
}
