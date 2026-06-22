package com.playzone.pems.application.comercial.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoEventoResponse {
    private String         codigo;
    private String         nombre;
    private String         descripcion;
    private String         icono;
    private boolean        esSistema;
    private boolean        activo;
    private int            orden;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
