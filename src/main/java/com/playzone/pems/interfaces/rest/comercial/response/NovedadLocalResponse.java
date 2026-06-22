package com.playzone.pems.interfaces.rest.comercial.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NovedadLocalResponse {
    private Long          id;
    private String        titulo;
    private String        descripcion;
    private String        imagenUrl;
    private String        textoCta;
    private String        urlCta;
    private int           prioridad;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private boolean       visibleHome;
    private boolean       destacada;
    private boolean       activa;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
