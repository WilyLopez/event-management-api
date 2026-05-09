package com.playzone.pems.application.contrato.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DocumentoContratoQuery {
    private Long          id;
    private String        nombre;
    private String        archivoUrl;
    private String        tipoArchivo;
    private Long          tamanobytes;
    private String        usuarioCarga;
    private LocalDateTime fechaCarga;
}