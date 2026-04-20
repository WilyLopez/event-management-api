package com.playzone.pems.domain.calendario.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TipoDia {

    SEMANA(
            "SEMANA",
            "Lunes a viernes (tarifa A)"
    ),
    FIN_SEMANA_FERIADO(
            "FIN_SEMANA_FERIADO",
            "Sábado, domingo y feriados (tarifa B)"
    );

    private final String codigo;
    private final String descripcion;

    public boolean esTarifaAlta() {
        return this == FIN_SEMANA_FERIADO;
    }

    public static TipoDia desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de día inválido: '" + codigo + "'"));
    }
}