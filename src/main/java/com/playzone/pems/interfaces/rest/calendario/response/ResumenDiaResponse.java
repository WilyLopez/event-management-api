package com.playzone.pems.interfaces.rest.calendario.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResumenDiaResponse {

    private LocalDate  fecha;
    private int        totalReservas;
    private int        totalEventos;
    private BigDecimal ingresoEstimado;
    private BigDecimal pagosPendientes;
    private int        aforoPublicoActual;
    private int        aforoMaximo;
    private TurnoResponse turnoT1;
    private TurnoResponse turnoT2;
    private List<ReservaResumen>  reservas;
    private List<EventoResumen>   eventos;
    private List<AlertaResumen>   alertas;

    @Getter
    @Builder
    public static class TurnoResponse {
        private boolean       disponible;
        private int           totalReservas;
        private EventoResumen eventoPrivado;
    }

    @Getter
    @Builder
    public static class ReservaResumen {
        private Long       id;
        private String     numeroTicket;
        private String     nombreNino;
        private String     nombreCliente;
        private String     estado;
        private BigDecimal totalPagado;
    }

    @Getter
    @Builder
    public static class EventoResumen {
        private Long    id;
        private String  tipoEvento;
        private String  turno;
        private String  horaInicio;
        private String  horaFin;
        private String  nombreCliente;
        private String  estado;
        private Integer aforoDeclarado;
    }

    @Getter
    @Builder
    public static class AlertaResumen {
        private String tipo;
        private String mensaje;
        private String nivel;
    }
}