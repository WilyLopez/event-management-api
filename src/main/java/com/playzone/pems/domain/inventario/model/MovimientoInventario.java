package com.playzone.pems.domain.inventario.model;

import com.playzone.pems.domain.inventario.model.enums.TipoMovimiento;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {

    private Long             id;
    private Long             idProducto;
    private TipoMovimiento   tipoMovimiento;
    private int              cantidad;
    private int              stockAnterior;
    private int              stockResultante;
    private String           motivo;
    private Long             idVenta;
    private Long             idUsuario;
    private LocalDateTime    fechaMovimiento;

    public boolean esCoherente() {
        return stockResultante == tipoMovimiento.calcularStockResultante(stockAnterior, cantidad);
    }

    public boolean esOriginadoPorVenta() {
        return idVenta != null;
    }
}