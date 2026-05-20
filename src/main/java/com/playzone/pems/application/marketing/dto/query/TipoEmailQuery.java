package com.playzone.pems.application.marketing.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoEmailQuery {

    private Long    id;
    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean activo;
}
