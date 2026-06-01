package com.playzone.pems.application.dashboard.dto.query;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservasDiaQuery {
    private final LocalDate fecha;
    private final int       cantidad;

    public ReservasDiaQuery(LocalDate fecha, Long cantidad) {
        this.fecha    = fecha;
        this.cantidad = cantidad != null ? cantidad.intValue() : 0;
    }
}
