package com.playzone.pems.domain.calendario.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TipoFeriado {

    NACIONAL(
            "NACIONAL",
            "Feriado oficial nacional del Perú"
    ),
    REGIONAL(
            "REGIONAL",
            "Feriado regional o local"
    );

    private final String codigo;
    private final String descripcion;

    public static TipoFeriado desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de feriado inválido: '" + codigo + "'"));
    }
}