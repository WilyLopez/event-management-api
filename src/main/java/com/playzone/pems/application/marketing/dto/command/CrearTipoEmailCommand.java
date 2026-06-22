package com.playzone.pems.application.marketing.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrearTipoEmailCommand {

    private String codigo;
    private String nombre;
    private String descripcion;
}
