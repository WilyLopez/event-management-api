package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ActualizarClientePerfilRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String  nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    private String  apellidoPaterno;

    private String  apellidoMaterno;
    private String  telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    private String  correo;

    private Boolean aceptaComunicaciones;
}
