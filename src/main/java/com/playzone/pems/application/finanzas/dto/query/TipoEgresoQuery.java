package com.playzone.pems.application.finanzas.dto.query;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoEgresoQuery {
    private Long            id;
    private String          nombre;
    private String          descripcion;
    private CategoriaEgreso categoria;
    private boolean         activo;
}
