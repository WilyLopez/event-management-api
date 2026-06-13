package com.playzone.pems.domain.evento.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CanalReserva {

    WEB(
            "WEB",
            "Reserva realizada desde el sitio web"
    ),
    MOSTRADOR(
            "MOSTRADOR",
            "Registro realizado en el local"
    );

    private final String codigo;
    private final String descripcion;

    public boolean esAutoservicio() {
        return this == WEB;
    }

    public static CanalReserva desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Canal de reserva inválido: '" + codigo + "'"));
    }
}