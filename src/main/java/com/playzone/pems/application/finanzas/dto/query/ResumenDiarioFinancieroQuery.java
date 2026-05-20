package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ResumenDiarioFinancieroQuery {
    private LocalDate  fecha;
    private BigDecimal ingresoReservas;
    private BigDecimal gastoOperativo;
    private BigDecimal utilidadDia;
    private int        cantidadReservas;
}
