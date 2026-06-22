package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ResumenRangoQuery {
    private LocalDate  inicio;
    private LocalDate  fin;
    private BigDecimal totalIngresoReservas;
    private BigDecimal totalEgresoGeneral;
    private BigDecimal totalEgresoOperativo;
    private BigDecimal totalEgresoNeto;
    private BigDecimal utilidadNeta;
    private long       cantidadReservas;
}
