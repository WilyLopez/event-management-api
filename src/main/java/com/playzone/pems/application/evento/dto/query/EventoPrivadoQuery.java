package com.playzone.pems.application.evento.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class EventoPrivadoQuery {

    private final Long       id;
    private final Long       idCliente;
    private final String     nombreCliente;
    private final String     correoCliente;
    private final String     telefonoCliente;
    private final Long       idSede;
    private final String     estado;
    private final Long       idTurno;
    private final String     turno;
    private final String     horaInicio;
    private final String     horaFin;
    private final LocalDate  fechaEvento;
    private final String     tipoEvento;
    private final String     contactoAdicional;
    private final Integer    aforoDeclarado;
    private final BigDecimal precioTotalContrato;
    private final BigDecimal montoAdelanto;
    private final BigDecimal montoSaldo;
    private final String     notasInternas;
    private final String     usuarioGestor;
    private final LocalDateTime fechaCreacion;
}