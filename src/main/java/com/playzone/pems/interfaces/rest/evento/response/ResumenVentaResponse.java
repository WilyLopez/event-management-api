package com.playzone.pems.interfaces.rest.evento.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenVentaResponse {
    private BigDecimal precioUnitario;
    private int        cantidadNinos;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private String     tipoDia;
    private int        aforoDisponible;
    private int        aforoMaximo;
}
