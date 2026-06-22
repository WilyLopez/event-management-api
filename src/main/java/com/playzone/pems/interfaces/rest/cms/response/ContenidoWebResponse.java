package com.playzone.pems.interfaces.rest.cms.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ContenidoWebResponse {
    private final Long          id;
    private final String        seccionCodigo;
    private final String        tipoContenidoCodigo;
    private final String        clave;
    private final String        valorEs;
    private final String        valorEn;
    private final String        imagenUrl;
    private final String        descripcion;
    private final int           ordenVisualizacion;
    private final boolean       visible;
    private final int           version;
    private final String        metadatos;
    private final boolean       activo;
    private final OffsetDateTime fechaActualizacion;
}
