package com.playzone.pems.interfaces.rest.comercial.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActividadLocalResponse {
    private Long          id;
    private String        nombre;
    private String        descripcion;
    private String        imagenUrl;
    private Long          idZona;
    private String        nombreZona;
    private boolean       esEspecial;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private boolean       activa;
    private boolean       destacada;
    private int           orden;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
