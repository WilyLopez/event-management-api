package com.playzone.pems.interfaces.rest.venta.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class VentaResponse {
    private final Long              id;
    private final Long              idSede;
    private final BigDecimal        subtotal;
    private final BigDecimal        descuento;
    private final BigDecimal        total;
    private final LocalDateTime     fechaVenta;
    private final List<DetalleVentaResponse> detalles;

    @Getter
    @Builder
    public static class DetalleVentaResponse {
        private final Long       idProducto;
        private final String     nombreProducto;
        private final int        cantidad;
        private final BigDecimal precioUnitario;
        private final BigDecimal subtotalLinea;
    }
}