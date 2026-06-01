package com.playzone.pems.interfaces.rest.comercial.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ServicioCotizacionResponse {
    private final Long       id;
    private final String     nombre;
    private final String     descripcion;
    private final BigDecimal precioReferencial;
    private final String     icono;
}
