package com.playzone.pems.application.finanzas.dto.command;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrearTipoIngresoCommand {
    private String          nombre;
    private String          descripcion;
    private CategoriaIngreso categoria;
}
