package com.playzone.pems.domain.contrato.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EstadoContrato {

    BORRADOR(
            "BORRADOR",
            "Contrato en redacción, no firmado"
    ),
    FIRMADO(
            "FIRMADO",
            "Contrato firmado por ambas partes"
    );

    private final String codigo;
    private final String descripcion;

    public boolean esEditable() {
        return this == BORRADOR;
    }

    public boolean esFirmado() {
        return this == FIRMADO;
    }

    public static EstadoContrato desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Estado de contrato inválido: '" + codigo + "'"));
    }
}