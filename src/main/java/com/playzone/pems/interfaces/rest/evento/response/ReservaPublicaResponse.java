package com.playzone.pems.interfaces.rest.evento.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservaPublicaResponse {
    private final Long        id;
    private final String      numeroTicket;
    private final String      estado;
    private final String      tipoDia;
    private final LocalDate   fechaEvento;
    private final String      nombreNino;
    private final int         edadNino;
    private final String      nombreAcompanante;
    private final BigDecimal  precioHistorico;
    private final BigDecimal  descuentoAplicado;
    private final BigDecimal  totalPagado;
    private final boolean     esReprogramacion;
    private final int         vecesReprogramada;
    private final boolean     firmoConsentimiento;
    private final LocalDateTime fechaCreacion;
}