package com.playzone.pems.domain.pago.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MedioPago {

    YAPE(
            "YAPE",
            "Pago vía Yape",
            true,
            true
    ),
    EFECTIVO(
            "EFECTIVO",
            "Pago en efectivo",
            true,
            false
    ),
    TRANSFERENCIA(
            "TRANSFERENCIA",
            "Transferencia bancaria",
            true,
            true
    ),
    TARJETA(
            "TARJETA",
            "Pago con tarjeta (uso futuro)",
            false,
            true
    );


    private final String  codigo;
    private final String  descripcion;

    private final boolean activo;

    private final boolean requiereReferencia;

    public boolean estaActivo() {
        return activo;
    }

    public boolean debeRegistrarReferencia() {
        return requiereReferencia;
    }

    public static MedioPago desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Medio de pago inválido: '" + codigo + "'"));
    }
}