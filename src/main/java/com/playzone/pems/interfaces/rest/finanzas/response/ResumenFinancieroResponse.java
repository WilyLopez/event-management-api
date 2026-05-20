package com.playzone.pems.interfaces.rest.finanzas.response;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ResumenFinancieroResponse {
    private int                          anio;
    private int                          mes;
    private BigDecimal                   totalIngresoReservas;
    private BigDecimal                   totalIngresoEventos;
    private BigDecimal                   totalIngresoOtros;
    private BigDecimal                   totalIngresoGeneral;
    private BigDecimal                   totalEgresoGeneral;
    private BigDecimal                   totalEgresoEventos;
    private BigDecimal                   totalEgresoOperativo;
    private BigDecimal                   totalEgresoNeto;
    private BigDecimal                   utilidadNeta;
    private List<DesgloseTipoEgreso>     desglosePorTipoEgreso;

    @Getter
    @Builder
    public static class DesgloseTipoEgreso {
        private String          nombreTipo;
        private CategoriaEgreso categoria;
        private BigDecimal      totalMonto;
    }
}
