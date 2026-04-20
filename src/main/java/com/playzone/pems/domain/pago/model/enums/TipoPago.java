package com.playzone.pems.domain.pago.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TipoPago {

    UNICO(
            "UNICO",
            "Pago único — cubre el total de la reserva",
            false   // No es parcial
    ),
    ADELANTO(
            "ADELANTO",
            "Adelanto — pago parcial inicial del evento privado",
            true
    ),
    SALDO(
            "SALDO",
            "Saldo — pago del monto restante del evento privado",
            true
    );

    private final String  codigo;
    private final String  descripcion;
    private final boolean esParcial;

    public boolean esParaReservaPublica() {
        return this == UNICO;
    }

    public boolean esParaEventoPrivado() {
        return this == ADELANTO || this == SALDO;
    }

    public static TipoPago desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tipo de pago inválido: '" + codigo + "'"));
    }
}