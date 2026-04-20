package com.playzone.pems.application.evento.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservaPublicaQuery {

    private final Long       id;
    private final Long       idCliente;
    private final String     nombreCliente;
    private final Long       idSede;
    private final String     estado;
    private final String     canalReserva;
    private final String     tipoDia;
    private final LocalDate  fechaEvento;
    private final String     numeroTicket;
    private final BigDecimal precioHistorico;
    private final BigDecimal descuentoAplicado;
    private final BigDecimal totalPagado;
    private final String     nombreNino;
    private final int        edadNino;
    private final String     nombreAcompanante;
    private final String     dniAcompanante;
    private final boolean    firmoConsentimiento;
    private final boolean    esReprogramacion;
    private final int        vecesReprogramada;
    private final LocalDateTime fechaCreacion;
}