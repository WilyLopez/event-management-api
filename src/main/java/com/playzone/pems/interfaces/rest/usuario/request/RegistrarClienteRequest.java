package com.playzone.pems.interfaces.rest.usuario.request;

import com.playzone.pems.shared.validation.DniValidator;
import com.playzone.pems.shared.validation.RucValidator;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegistrarClienteRequest {

    @NotBlank @Size(max = 120)
    private String nombre;

    @NotBlank @Email @Size(max = 120)
    private String correo;

    @NotBlank @Size(min = 8, max = 72)
    private String contrasena;

    @NotBlank @Size(max = 20)
    private String telefono;

    @DniValidator(requerido = false)
    private String dni;

    @RucValidator(requerido = false)
    private String ruc;

    @Size(max = 200)
    private String razonSocial;

    @Size(max = 300)
    private String direccionFiscal;
}