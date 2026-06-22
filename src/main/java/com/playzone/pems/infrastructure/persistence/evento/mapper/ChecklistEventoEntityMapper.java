package com.playzone.pems.infrastructure.persistence.evento.mapper;

import com.playzone.pems.domain.evento.model.ChecklistEvento;
import com.playzone.pems.infrastructure.persistence.evento.entity.ChecklistEventoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

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
                .idUsuarioCompleto(e.getCompletadaPor())
                .nombreUsuarioCompleto(null)
                .fechaCompletado(e.getFechaCompletado() != null ? e.getFechaCompletado() : null)
                .fechaCreacion(e.getFechaCreacion() != null ? e.getFechaCreacion() : null)
                .build();
    }

    public ChecklistEventoEntity toEntity(ChecklistEvento d, EventoPrivadoEntity evento) {
        return ChecklistEventoEntity.builder()
                .id(d.getId())
                .eventoPrivado(evento)
                .tarea(d.getTarea())
                .completada(d.isCompletada())
                .orden(d.getOrden())
                .completadaPor(d.getIdUsuarioCompleto())
                .fechaCompletado(d.getFechaCompletado() != null ? d.getFechaCompletado() : null)
                .build();
    }
}
