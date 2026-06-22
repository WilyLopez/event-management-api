package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.TipoMovimientoCaja;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCaja {
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
    private UUID               idUsuarioRegistra;
    private OffsetDateTime      fechaCreacion;
}
