package com.playzone.pems.interfaces.rest.evento.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class EventoPrivadoResponse {
    private final Long        id;
    private final String      estado;
    private final String      turno;
    private final String      horaInicio;
    private final String      horaFin;
    private final LocalDate   fechaEvento;
    private final String      tipoEvento;
    private final String      contactoAdicional;
    private final Integer     aforoDeclarado;
    private final BigDecimal  precioTotalContrato;
    private final BigDecimal  montoAdelanto;
    private final BigDecimal  montoSaldo;
    private final String      nombreCliente;
    private final String      correoCliente;
    private final LocalDateTime fechaCreacion;
}