package com.playzone.pems.domain.venta.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    private Long       id;
    private Long       idVenta;
    private Long       idProducto;
    private int        cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotalLinea;

    public boolean subtotalEsCoherente() {
        BigDecimal esperado = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return subtotalLinea.compareTo(esperado) == 0;
    }

    public BigDecimal calcularSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}