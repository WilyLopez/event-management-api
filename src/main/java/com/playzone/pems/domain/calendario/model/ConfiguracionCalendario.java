package com.playzone.pems.domain.calendario.model;

import lombok.*;

import java.time.LocalTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionCalendario {

    private Long      idConfig;
    private Long      idSede;
    private int       diasMinReservaPublica;
    private int       diasMaxReservaPublica;
    private int       diasMinEventoPrivado;
    private int       diasMaxEventoPrivado;
    private int       aforoMaximo;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private LocalTime turnoT1Inicio;
    private LocalTime turnoT1Fin;
    private LocalTime turnoT2Inicio;
    private LocalTime turnoT2Fin;
    private String    diasOperacion;
    private int       rangoMaxBloqueo;
    private int       edadMinCumple;
    private int       edadMaxCumple;
}
