package com.playzone.pems.domain.contrato.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoContrato {

    private Long          id;
    private Long          idContrato;
    private String        nombre;
    private String        archivoUrl;
    private String        tipoArchivo;
    private Long          tamanobytes;
    private UUID          idUsuarioCarga;
    private String        nombreUsuarioCarga;
    private OffsetDateTime fechaCarga;
}