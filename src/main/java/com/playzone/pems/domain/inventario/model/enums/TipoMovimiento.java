package com.playzone.pems.domain.inventario.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TipoMovimiento {

    ENTRADA(
            "ENTRADA",
            "Ingreso de mercancía al inventario",
            1
    ),
    SALIDA(
            "SALIDA",
            "Salida de mercancía del inventario (venta o consumo)",
            -1
    ),
    AJUSTE(
            "AJUSTE",
            "Ajuste manual por conteo físico o corrección",
            0
    ),
    BAJA(
            "BAJA",
            "Baja por merma, vencimiento o daño irreparable",
            -1
    );

    private final String codigo;
    private final String descripcion;
    private final int factorImpacto;

    public boolean incrementaStock() {
        return factorImpacto > 0;
    }

    public boolean decrementaStock() {
        return factorImpacto < 0;
    }


    public boolean esAjusteDirecto() {
        return this == AJUSTE;
    }

    public int calcularStockResultante(int stockAnterior, int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad del movimiento no puede ser negativa.");
        }
        return switch (this) {
            case ENTRADA -> stockAnterior + cantidad;
            case SALIDA, BAJA -> Math.max(0, stockAnterior - cantidad);
            case AJUSTE -> cantidad;
        };
    }

    public static TipoMovimiento desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tipo de movimiento inválido: '" + codigo + "'"));
    }
}