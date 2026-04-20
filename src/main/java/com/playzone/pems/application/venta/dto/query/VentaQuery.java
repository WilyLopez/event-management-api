package com.playzone.pems.application.venta.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class VentaQuery {

    private final Long              id;
    private final Long              idSede;
    private final String            usuarioVendedor;
    private final Long              idReservaPublica;
    private final Long              idEventoPrivado;
    private final BigDecimal        subtotal;
    private final BigDecimal        descuento;
    private final BigDecimal        total;
    private final LocalDateTime     fechaVenta;
    private final List<DetalleVentaQuery> detalles;

    @Getter
    @Builder
    public static class DetalleVentaQuery {
        private final Long       idProducto;
        private final String     nombreProducto;
        private final int        cantidad;
        private final BigDecimal precioUnitario;
        private final BigDecimal subtotalLinea;
    }
}