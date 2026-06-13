package com.playzone.pems.domain.evento.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistEvento {

    private Long          id;
    private Long          idEventoPrivado;
    private String        tarea;
    private boolean       completada;
    private int           orden;
    private UUID          idUsuarioCompleto;
    private String        nombreUsuarioCompleto;
    private OffsetDateTime fechaCompletado;
    private OffsetDateTime fechaCreacion;
}