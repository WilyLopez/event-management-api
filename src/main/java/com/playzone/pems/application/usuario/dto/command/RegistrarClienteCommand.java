package com.playzone.pems.application.usuario.dto.command;

import com.playzone.pems.shared.validation.DniValidator;
import com.playzone.pems.shared.validation.RucValidator;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrarClienteCommand {

    @NotBlank
    @Size(max = 120)
    private final String nombre;

    @NotBlank
    @Email
    @Size(max = 120)
    private final String correo;

    @NotBlank
    @Size(min = 8, max = 72)
    private final String contrasena;

    @NotBlank
    @Size(max = 20)
    private final String telefono;

    @DniValidator(requerido = false)
    private final String dni;

    @RucValidator(requerido = false)
    private final String ruc;

    @Size(max = 200)
    private final String razonSocial;

    @Size(max = 300)
    private final String direccionFiscal;
}