package com.playzone.pems.application.evento.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class MetricasReservaQuery {

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