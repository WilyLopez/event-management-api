package com.playzone.pems.domain.evento.model.enums;

import java.util.Arrays;

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

    CanalReserva(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }

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