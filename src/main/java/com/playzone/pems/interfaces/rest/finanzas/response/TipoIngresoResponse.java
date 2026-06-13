package com.playzone.pems.interfaces.rest.finanzas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoIngresoResponse {
    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean esSistema;
    private int     orden;
    private boolean activo;
}
