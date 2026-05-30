package com.playzone.pems.interfaces.rest.finanzas.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ResumenDiarioResponse {
    private LocalDate  fecha;
    private BigDecimal ingresoReservas;
    private BigDecimal gastoOperativo;
    private BigDecimal utilidadDia;
    private int        cantidadReservas;
    private BigDecimal ticketPromedio;
}
