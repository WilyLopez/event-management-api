package com.playzone.pems.application.finanzas.dto.query;

import com.playzone.pems.domain.finanzas.model.enums.TipoMovimientoCaja;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class MovimientoCajaQuery {
    private Long               id;
    private Long               idAperturaCaja;
    private TipoMovimientoCaja tipo;
    private String             concepto;
    private BigDecimal         monto;
    private String             medioPago;
    private Long               idRegistroIngreso;
    private Long               idRegistroEgreso;
    private Long               idVenta;
    private boolean            esManual;
    private OffsetDateTime      fechaCreacion;
}
