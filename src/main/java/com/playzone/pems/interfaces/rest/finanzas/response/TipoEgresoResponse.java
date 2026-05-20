package com.playzone.pems.interfaces.rest.finanzas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoEgresoResponse {
    private Long            id;
    private String          nombre;
    private String          descripcion;
    private CategoriaEgreso categoria;
    private boolean         activo;
}
