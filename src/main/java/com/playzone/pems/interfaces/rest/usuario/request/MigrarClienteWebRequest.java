package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class MigrarClienteWebRequest {

    @NotBlank
    @Email
    @Size(max = 120)
    private String correo;

    @NotBlank
    @Size(min = 8, max = 72)
    private String contrasena;

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotBlank
    @Size(max = 20)
    private String telefono;
}
