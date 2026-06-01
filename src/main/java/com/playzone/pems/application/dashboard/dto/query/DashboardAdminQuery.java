package com.playzone.pems.application.dashboard.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DashboardAdminQuery {
    private final LocalDate fecha;
    private final int       reservasHoy;
    private final int       reservasConfirmadas;
    private final int       pendientesPago;
    private final int       aforoMaximo;
    private final int       plazasDisponibles;
    private final int       eventosEstaSemana;
    private final int       solicitudesEventoSinResponder;
    private final int       eventosSaldoPendiente;
    private final boolean   cajaAbierta;

    private final List<AgendaReservaQuery>    reservasHoyDetalle;
    private final List<AgendaEventoQuery>     eventosHoyDetalle;
    private final List<ReservasDiaQuery>      reservasUltimos30Dias;
    private final List<DisponibilidadDiaQuery> disponibilidadSemana;
}
