package com.playzone.pems.interfaces.rest.usuario.request;

import lombok.Getter;

@Getter
public class ActualizarClientePerfilRequest {

    private String  nombres;
    private String  apellidoPaterno;
    private String  apellidoMaterno;
    private String  telefono;
    private String  correo;
    private Boolean aceptaComunicaciones;
}
