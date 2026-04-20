package com.playzone.pems.application.contrato.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ContratoQuery {

    private final Long      id;
    private final Long      idEventoPrivado;
    private final String    estado;
    private final String    contenidoTexto;
    private final String    archivoPdfUrl;
    private final LocalDate fechaFirma;
    private final String    usuarioRedactor;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime fechaActualizacion;
}