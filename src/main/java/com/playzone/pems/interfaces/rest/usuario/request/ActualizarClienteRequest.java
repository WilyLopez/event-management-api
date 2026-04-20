package com.playzone.pems.interfaces.rest.usuario.request;

import com.playzone.pems.shared.validation.RucValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ActualizarClienteRequest {

    @NotBlank @Size(max = 120)
    private String nombre;

    @NotBlank @Size(max = 20)
    private String telefono;

    @RucValidator(requerido = false)
    private String ruc;

    @Size(max = 200)
    private String razonSocial;

    @Size(max = 300)
    private String direccionFiscal;
}