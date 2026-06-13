package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoIngresoQuery {
    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean esSistema;
    private int     orden;
    private boolean activo;
}
