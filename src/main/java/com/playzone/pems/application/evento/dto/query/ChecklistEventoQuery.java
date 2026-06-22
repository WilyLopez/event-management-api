package com.playzone.pems.application.evento.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ChecklistEventoQuery {

    private Long          id;
    private Long          idEventoPrivado;
    private String        tarea;
    private boolean       completada;
    private int           orden;
    private String        usuarioCompleto;
    private OffsetDateTime fechaCompletado;
}