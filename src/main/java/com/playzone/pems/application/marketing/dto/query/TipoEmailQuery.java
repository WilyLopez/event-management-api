package com.playzone.pems.application.marketing.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoEmailQuery {

    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean esSistema;
    private int     orden;
    private boolean activo;
}
