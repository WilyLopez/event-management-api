package com.playzone.pems.application.usuario.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActualizarClientePerfilCommand {

    private String  nombres;
    private String  apellidoPaterno;
    private String  apellidoMaterno;
    private String  telefono;
    private String  correo;
    private Boolean aceptaComunicaciones;
}
