package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RegistrarClienteAdminRequest {

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @Email
    @Size(max = 120)
    private String correo;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @Size(max = 8)
    private String dni;

    private LocalDate fechaNacimiento;

    @Size(max = 500)
    private String observaciones;

    private String tipoCliente;

    private boolean aceptaComunicaciones = true;
}
