package com.playzone.pems.application.facturacion.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnularComprobanteCommand {

    @NotNull
    private final Long idComprobante;

    @NotBlank
    @Size(max = 300)
    private final String motivoAnulacion;

    @NotNull
    private final Long idUsuario;
}