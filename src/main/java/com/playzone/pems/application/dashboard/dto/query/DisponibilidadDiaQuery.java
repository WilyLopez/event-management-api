package com.playzone.pems.application.dashboard.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DisponibilidadDiaQuery {
    private final LocalDate fecha;
    private final boolean   turnoT1Disponible;
    private final boolean   turnoT2Disponible;
    private final int       totalEventos;
}
