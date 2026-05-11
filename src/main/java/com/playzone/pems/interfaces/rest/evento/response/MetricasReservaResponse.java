package com.playzone.pems.interfaces.rest.evento.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class MetricasReservaResponse {

    private LocalDate  fecha;
    private int        totalReservas;
    private int        pendientes;
    private int        confirmadas;
    private int        canceladas;
    private int        ingresados;
    private int        aforoMaximo;
    private int        aforoOcupado;
    private int        aforoRestante;
    private BigDecimal ingresosDia;
}