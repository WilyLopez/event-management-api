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
    private final String        fotoUrl;
    private final String        respuestaAdmin;
    private final LocalDateTime fechaRespuesta;
    private final boolean       destacada;
    private final boolean       mostrarHome;
    private final LocalDateTime fechaCreacion;
}
