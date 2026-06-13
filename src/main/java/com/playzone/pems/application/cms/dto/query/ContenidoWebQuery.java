package com.playzone.pems.application.cms.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ContenidoWebQuery {

    private final Long          id;
    private final String        seccion;
    private final String        tipoContenido;
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
