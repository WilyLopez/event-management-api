package com.playzone.pems.interfaces.rest.comercial.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZonaJuegoResponse {
    private Long          id;
    private String        nombre;
    private String        slug;
    private String        descripcion;
    private Integer       edadMinima;
    private Integer       edadMaxima;
    private boolean       activa;
    private boolean       destacada;
    private int           orden;
    private List<String>  imagenes;
    private List<String>  videos;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
