package com.playzone.pems.interfaces.rest.dashboard.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DashboardAdminResponse {

    private final LocalDate  fecha;
    private final int        reservasHoy;
    private final int        reservasAyer;
    private final BigDecimal ingresosHoy;
    private final int        reservasConfirmadas;
    private final int        pendientesPago;
    private final int       aforoMaximo;
    private final int       plazasDisponibles;
    private final int       eventosEstaSemana;
    private final int       solicitudesEventoSinResponder;
    private final int       eventosSaldoPendiente;
    private final boolean   cajaAbierta;

    private final List<AgendaReservaResponse>    reservasHoyDetalle;
    private final List<AgendaEventoResponse>     eventosHoyDetalle;
    private final List<ReservasDiaResponse>      reservasUltimos30Dias;
    private final List<DisponibilidadDiaResponse> disponibilidadSemana;

    @Getter @Builder
    public static class AgendaReservaResponse {
        private final String numeroTicket;
        private final String nombreNino;
        private final int    edadNino;
        private final String estado;
    }

    @Getter @Builder
    public static class AgendaEventoResponse {
        private final Long   id;
        private final String tipoEvento;
        private final String nombreCliente;
        private final String turno;
        private final String estado;
    }

    @Getter @Builder
    public static class ReservasDiaResponse {
        private final LocalDate fecha;
        private final int       cantidad;
    }

    @Getter @Builder
    public static class DisponibilidadDiaResponse {
        private final LocalDate fecha;
        private final boolean   turnoT1Disponible;
        private final boolean   turnoT2Disponible;
        private final int       totalEventos;
    }
}
