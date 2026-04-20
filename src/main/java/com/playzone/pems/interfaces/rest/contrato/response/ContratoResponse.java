package com.playzone.pems.interfaces.rest.contrato.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ContratoResponse {
    private final Long        id;
    private final Long        idEventoPrivado;
    private final String      estado;
    private final String      archivoPdfUrl;
    private final LocalDate   fechaFirma;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime fechaActualizacion;
}