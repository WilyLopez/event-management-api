package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ResumenFinancieroQuery {
    private int                        anio;
    private int                        mes;
    private BigDecimal                 totalIngresoReservas;
    private BigDecimal                 totalAdelantoEventos;
    private BigDecimal                 totalIngresoOtros;
    private BigDecimal                 totalIngresoGeneral;
    private BigDecimal                 totalEgresoGeneral;
    private BigDecimal                 totalEgresoEventos;
    private BigDecimal                 totalEgresoOperativo;
    private BigDecimal                 totalEgresoNeto;
    private BigDecimal                 utilidadNeta;
    private List<DesgloseTipoEgresoQuery> desglosePorTipoEgreso;
}
