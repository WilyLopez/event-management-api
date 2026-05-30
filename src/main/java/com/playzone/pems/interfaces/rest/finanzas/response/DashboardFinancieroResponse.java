package com.playzone.pems.interfaces.rest.finanzas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardFinancieroResponse {
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
}
