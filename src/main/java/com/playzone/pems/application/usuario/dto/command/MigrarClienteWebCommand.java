package com.playzone.pems.application.usuario.dto.command;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MigrarClienteWebCommand {

    @NotBlank
    @Email
    @Size(max = 120)
    private final String correo;

    @NotBlank
    @Size(min = 8, max = 72)
    private final String contrasena;

    @NotBlank
    @Size(max = 120)
    private final String nombre;

    @NotBlank
    @Size(max = 20)
    private final String telefono;
}
