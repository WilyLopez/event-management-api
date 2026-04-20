package com.playzone.pems.interfaces.rest.cms.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContenidoWebResponse {
    private final Long          id;
    private final Long          idSeccion;
    private final String        clave;
    private final String        valorEs;
    private final String        valorEn;
    private final boolean       activo;
    private final LocalDateTime fechaActualizacion;
}