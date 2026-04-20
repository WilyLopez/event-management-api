package com.playzone.pems.application.proveedor.dto.command;

import com.playzone.pems.shared.validation.RucValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GestionarProveedorCommand {

    @NotBlank
    @Size(max = 200)
    private final String nombre;

    @RucValidator(requerido = false)
    private final String ruc;

    @Size(max = 120)
    private final String contactoNombre;

    @Size(max = 20)
    private final String contactoTelefono;

    @Email
    @Size(max = 120)
    private final String contactoCorreo;

    @NotBlank
    @Size(max = 200)
    private final String tipoServicio;

    private final String notas;
}