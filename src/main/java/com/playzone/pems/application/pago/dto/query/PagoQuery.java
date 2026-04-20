package com.playzone.pems.application.pago.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PagoQuery {

    private final Long          id;
    private final String        medioPago;
    private final String        tipoPago;
    private final Long          idReservaPublica;
    private final Long          idEventoPrivado;
    private final Long          idVenta;
    private final BigDecimal    monto;
    private final String        referenciaPago;
    private final boolean       esParcial;
    private final LocalDateTime fechaPago;
}