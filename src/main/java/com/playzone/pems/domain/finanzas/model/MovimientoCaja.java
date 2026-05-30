package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.TipoMovimientoCaja;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Long               idReservaPublica;
    private boolean            esManual;
    private Long               idUsuarioRegistra;
    private LocalDateTime      fechaCreacion;
}
