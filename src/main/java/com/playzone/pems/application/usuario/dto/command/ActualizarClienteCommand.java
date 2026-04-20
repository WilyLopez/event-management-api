package com.playzone.pems.application.usuario.dto.command;

import com.playzone.pems.shared.validation.RucValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActualizarClienteCommand {

    @NotBlank
    @Size(max = 120)
    private final String nombre;

    @NotBlank
    @Size(max = 20)
    private final String telefono;

    @RucValidator(requerido = false)
    private final String ruc;

    @Size(max = 200)
    private final String razonSocial;

    @Size(max = 300)
    private final String direccionFiscal;
}