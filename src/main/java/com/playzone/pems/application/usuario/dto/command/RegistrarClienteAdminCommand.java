package com.playzone.pems.application.usuario.dto.command;

import com.playzone.pems.shared.validation.DniValidator;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RegistrarClienteAdminCommand {

    @NotBlank
    @Size(max = 120)
    private final String nombre;

    @Email
    @Size(max = 120)
    private final String correo;

    @NotBlank
    @Size(max = 20)
    private final String telefono;

    @DniValidator(requerido = false)
    private final String dni;

    private final LocalDate fechaNacimiento;

    @Size(max = 500)
    private final String observaciones;

    private final String tipoCliente;

    private final boolean aceptaComunicaciones;
}
