package com.playzone.pems.infrastructure.persistence.evento.mapper;

import com.playzone.pems.domain.evento.model.ChecklistEvento;
import com.playzone.pems.infrastructure.persistence.evento.entity.ChecklistEventoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class ChecklistEventoEntityMapper {

    public ChecklistEvento toDomain(ChecklistEventoEntity e) {
        if (e == null) return null;
        return ChecklistEvento.builder()
                .id(e.getId())
                .idEventoPrivado(e.getEventoPrivado().getId())
                .tarea(e.getTarea())
                .completada(e.isCompletada())
                .orden(e.getOrden())
                .idUsuarioCompleto(e.getUsuarioCompleto() != null ? e.getUsuarioCompleto().getId() : null)
                .nombreUsuarioCompleto(e.getUsuarioCompleto() != null ? e.getUsuarioCompleto().getNombre() : null)
                .fechaCompletado(e.getFechaCompletado())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }

    public ChecklistEventoEntity toEntity(ChecklistEvento d,
                                           EventoPrivadoEntity evento,
                                           UsuarioAdminEntity usuario) {
        return ChecklistEventoEntity.builder()
                .id(d.getId())
                .eventoPrivado(evento)
                .tarea(d.getTarea())
                .completada(d.isCompletada())
                .orden(d.getOrden())
                .usuarioCompleto(usuario)
                .fechaCompletado(d.getFechaCompletado())
                .build();
    }
}