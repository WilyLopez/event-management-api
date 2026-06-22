package com.playzone.pems.domain.comercial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServicioCotizacion {
    private Long       id;
    private String     nombre;
    private String     descripcion;
    private BigDecimal precioReferencial;
    private String     icono;
    private boolean    activo;
    private int        orden;
}
