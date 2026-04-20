package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank @Email
    private String correo;

    @NotBlank
    private String contrasena;
}