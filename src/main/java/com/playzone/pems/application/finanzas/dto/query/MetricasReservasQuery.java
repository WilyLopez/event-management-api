package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MetricasReservasQuery {
    private int        anio;
    private int        mes;
    private long       totalConfirmadas;
    private long       totalCanceladas;
    private long       totalCompletadas;
    private BigDecimal ingresoTotal;
    private BigDecimal ticketPromedio;
    private BigDecimal ingresoEfectivo;
    private BigDecimal ingresoYape;
}
