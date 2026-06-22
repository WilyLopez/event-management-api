package com.playzone.pems.interfaces.rest.finanzas.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MetricasReservasResponse {
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
