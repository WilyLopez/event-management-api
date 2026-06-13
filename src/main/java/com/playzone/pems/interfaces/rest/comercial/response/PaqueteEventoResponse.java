package com.playzone.pems.interfaces.rest.comercial.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaqueteEventoResponse {
    private Long          id;
    private String        nombre;
    private String        slug;
    private String        descripcionCorta;
    private String        descripcionLarga;
    private BigDecimal    precio;
    private String        badge;
    private String        color;
    private String        imagenUrl;
    private Integer       duracionMinutos;
    private Integer       limitepersonas;
    private boolean       activo;
    private boolean       destacado;
    private int           orden;
    private List<String>  beneficios;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
