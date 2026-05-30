package com.playzone.pems.application.finanzas.dto.query;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoIngresoQuery {
    private Long            id;
    private String          nombre;
    private String          descripcion;
    private CategoriaIngreso categoria;
    private boolean         activo;
}
