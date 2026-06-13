package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrearTipoIngresoCommand {
    private String codigo;
    private String nombre;
    private String descripcion;
}
