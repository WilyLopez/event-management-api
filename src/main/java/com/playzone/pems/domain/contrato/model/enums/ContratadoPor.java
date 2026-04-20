package com.playzone.pems.domain.contrato.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ContratadoPor {

    EMPRESA(
            "EMPRESA",
            "Contratado y gestionado por PlayZone"
    ),
    CLIENTE(
            "CLIENTE",
            "Proveedor traído directamente por el cliente"
    );

    private final String codigo;
    private final String descripcion;

    public boolean esACostoDeLaEmpresa() {
        return this == EMPRESA;
    }

    public static ContratadoPor desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Valor de 'contratadoPor' inválido: '" + codigo + "'"));
    }
}