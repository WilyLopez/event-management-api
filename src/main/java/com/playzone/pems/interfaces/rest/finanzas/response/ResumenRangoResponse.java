package com.playzone.pems.interfaces.rest.finanzas.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ResumenRangoResponse {
    private LocalDate  inicio;
    private LocalDate  fin;
    private BigDecimal totalIngresoReservas;
    private BigDecimal totalEgresoGeneral;
    private BigDecimal totalEgresoOperativo;
    private BigDecimal totalEgresoNeto;
    private BigDecimal utilidadNeta;
    private long       cantidadReservas;
}
