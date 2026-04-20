package com.playzone.pems.domain.inventario.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    private Long          id;
    private Long          idCategoria;
    private Long          idSede;
    private String        nombre;
    private String        descripcion;
    private BigDecimal    precio;
    private int           stockActual;
    private int           stockMinimo;
    private String        unidadMedida;
    private boolean       activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public boolean tieneStockSuficiente(int cantidadSolicitada) {
        return activo && stockActual >= cantidadSolicitada;
    }

    public boolean estaEnAlertaDeStock() {
        return stockActual <= stockMinimo;
    }

    public int unidadesParaReponer() {
        return Math.max(0, stockMinimo - stockActual);
    }

    public BigDecimal valorTotalInventario() {
        return precio.multiply(BigDecimal.valueOf(stockActual));
    }
}