package com.playzone.pems.interfaces.rest.pago.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PagoResponse {
    private final Long          id;
    private final String        medioPago;
    private final String        tipoPago;
    private final BigDecimal    monto;
    private final String        referenciaPago;
    private final boolean       esParcial;
    private final LocalDateTime fechaPago;
}