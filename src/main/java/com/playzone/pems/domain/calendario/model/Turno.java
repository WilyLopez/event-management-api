package com.playzone.pems.domain.calendario.model;

import lombok.*;

import java.time.LocalTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Turno {

    private Long      id;
    private String    codigo;
    private String    descripcion;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public long duracionEnHoras() {
        return java.time.Duration.between(horaInicio, horaFin).toHours();
    }

    public String etiquetaHorario() {
        return String.format("%s (%s – %s)", codigo, horaInicio, horaFin);
    }
}