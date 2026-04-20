package com.playzone.pems.application.calendario.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DisponibilidadQuery {

    private final Long      idSede;
    private final LocalDate fecha;
    private final boolean   accesoPublicoActivo;
    private final boolean   turnoT1Disponible;
    private final boolean   turnoT2Disponible;
    private final int       aforoPublicoActual;
    private final int       aforoMaximo;
    private final int       plazasDisponibles;
    private final boolean   aforoCompleto;
    private final boolean   bloqueadoManualmente;
    private final String    tipoDia;
}