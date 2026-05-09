package com.playzone.pems.application.calendario.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ResumenDiaQuery {

    private LocalDate    fecha;
    private int          totalReservas;
    private int          totalEventos;
    private BigDecimal   ingresoEstimado;
    private BigDecimal   pagosPendientes;
    private int          aforoPublicoActual;
    private int          aforoMaximo;
    private ResumenTurno turnoT1;
    private ResumenTurno turnoT2;
    private List<ResumenReservaQuery>  reservas;
    private List<ResumenEventoQuery>   eventos;
    private List<AlertaDiaQuery>       alertas;

    @Getter
    @Builder
    public static class ResumenTurno {
        private boolean          disponible;
        private int              totalReservas;
        private ResumenEventoQuery eventoPrivado;
    }

    @Getter
    @Builder
    public static class ResumenReservaQuery {
        private Long   id;
        private String numeroTicket;
        private String nombreNino;
        private String nombreCliente;
        private String estado;
        private BigDecimal totalPagado;
    }

    @Getter
    @Builder
    public static class ResumenEventoQuery {
        private Long   id;
        private String tipoEvento;
        private String turno;
        private String horaInicio;
        private String horaFin;
        private String nombreCliente;
        private String estado;
        private Integer aforoDeclarado;
    }

    @Getter
    @Builder
    public static class AlertaDiaQuery {
        private String tipo;
        private String mensaje;
        private String nivel;
    }
}