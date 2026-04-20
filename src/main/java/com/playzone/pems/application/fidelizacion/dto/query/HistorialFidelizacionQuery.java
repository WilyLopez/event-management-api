package com.playzone.pems.application.fidelizacion.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistorialFidelizacionQuery {

    private final Long          id;
    private final Long          idCliente;
    private final Long          idReservaPublica;
    private final int           visitaNumero;
    private final boolean       esBeneficioAplicado;
    private final LocalDateTime fechaRegistro;
}