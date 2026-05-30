package com.playzone.pems.interfaces.rest.finanzas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoIngresoResponse {
    private Long             id;
    private String           nombre;
    private String           descripcion;
    private CategoriaIngreso categoria;
    private boolean          activo;
}
