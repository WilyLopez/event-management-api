package com.playzone.pems.domain.comercial.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ServicioCotizacion {
    private final Long       id;
    private final String     nombre;
    private final String     descripcion;
    private final BigDecimal precioReferencial;
    private final String     icono;
}
