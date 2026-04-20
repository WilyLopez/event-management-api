package com.playzone.pems.interfaces.rest.calendario.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DisponibilidadResponse {
    private final Long      idSede;
    private final LocalDate fecha;
    private final String    tipoDia;
    private final boolean   accesoPublicoActivo;
    private final boolean   turnoT1Disponible;
    private final boolean   turnoT2Disponible;
    private final int       aforoPublicoActual;
    private final int       aforoMaximo;
    private final int       plazasDisponibles;
    private final boolean   aforoCompleto;
    private final boolean   bloqueadoManualmente;
}