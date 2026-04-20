package com.playzone.pems.interfaces.rest.cms.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResenaResponse {
    private final Long          id;
    private final String        nombreAutor;
    private final String        contenido;
    private final int           calificacion;
    private final boolean       aprobada;
    private final LocalDateTime fechaCreacion;
}