package com.playzone.pems.domain.evento.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Long          idUsuarioCompleto;
    private String        nombreUsuarioCompleto;
    private LocalDateTime fechaCompletado;
    private LocalDateTime fechaCreacion;
}