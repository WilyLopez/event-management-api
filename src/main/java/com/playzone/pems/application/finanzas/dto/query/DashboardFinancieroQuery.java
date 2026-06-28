package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardFinancieroQuery {
    private int        anio;
    private int        mes;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal utilidadNeta;
    private BigDecimal ingresoReservas;
    private BigDecimal ingresoAdelantos;
    private BigDecimal ingresoManual;
    private BigDecimal egresoFijo;
    private BigDecimal egresoVariable;
    private BigDecimal egresoEventual;
    private long       reservasConfirmadas;
    private long       reservasCanceladas;
    private BigDecimal ticketPromedio;
    private BigDecimal saldoPendienteEventos;
    private BigDecimal totalIngresosMesAnterior;
    private BigDecimal totalEgresosMesAnterior;
    private BigDecimal utilidadMesAnterior;
    private List<SerieDiaQuery> serieDiaria;
}
