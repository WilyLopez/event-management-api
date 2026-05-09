package com.playzone.pems.domain.contrato.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Long          idUsuarioCarga;
    private String        nombreUsuarioCarga;
    private LocalDateTime fechaCarga;
}