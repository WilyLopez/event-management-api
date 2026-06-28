package com.playzone.pems.interfaces.rest.finanzas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private BigDecimal totalIngresosMesAnterior;
    private BigDecimal totalEgresosMesAnterior;
    private BigDecimal utilidadMesAnterior;
    private List<SerieDiaResponse> serieDiaria;

    @Getter
    @Builder
    public static class SerieDiaResponse {
        private LocalDate  fecha;
        private BigDecimal ingresos;
        private BigDecimal egresos;
    }
}
