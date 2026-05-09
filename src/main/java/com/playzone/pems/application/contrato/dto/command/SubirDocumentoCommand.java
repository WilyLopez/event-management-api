package com.playzone.pems.application.contrato.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubirDocumentoCommand {
    private Long   idContrato;
    private String nombre;
    private String archivoUrl;
    private String tipoArchivo;
    private Long   tamanobytes;
    private Long   idUsuarioCarga;
}