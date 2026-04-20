package com.playzone.pems.domain.promocion.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;


@Getter
@RequiredArgsConstructor
public enum TipoPromocion {

    DESCUENTO_PORCENTAJE(
            "DESCUENTO_PORCENTAJE",
            "Descuento porcentual sobre el precio base",
            true
    ),
    DESCUENTO_MONTO_FIJO(
            "DESCUENTO_MONTO_FIJO",
            "Descuento de monto fijo sobre el precio base",
            true
    ),
    PAQUETE_GRUPAL(
            "PAQUETE_GRUPAL",
            "Precio especial para grupos",
            true
    ),
    ENTRADA_GRATUITA(
            "ENTRADA_GRATUITA",
            "Entrada sin costo (uso interno para fidelización)",
            false
    ),
    CLIENTE_FRECUENTE(
            "CLIENTE_FRECUENTE",
            "Beneficio acumulativo por visitas",
            false
    );

    private final String  codigo;
    private final String  descripcion;

    private final boolean requiereValorDescuento;

    public BigDecimal calcularDescuento(BigDecimal precioBase, BigDecimal valorDescuento) {
        if (precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return switch (this) {
            case DESCUENTO_PORCENTAJE -> {
                BigDecimal porcentaje = valorDescuento
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal descuento = precioBase.multiply(porcentaje)
                        .setScale(2, RoundingMode.HALF_UP);
                yield descuento.min(precioBase);
            }
            case DESCUENTO_MONTO_FIJO, PAQUETE_GRUPAL ->
                    valorDescuento.min(precioBase).max(BigDecimal.ZERO);
            case ENTRADA_GRATUITA, CLIENTE_FRECUENTE ->
                    precioBase; // Descuento total: entrada gratuita
        };
    }

    public boolean esEntradaGratuita() {
        return this == ENTRADA_GRATUITA || this == CLIENTE_FRECUENTE;
    }

    public boolean requiereMinimoPersonas() {
        return this == PAQUETE_GRUPAL;
    }

    public static TipoPromocion desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tipo de promoción inválido: '" + codigo + "'"));
    }
}