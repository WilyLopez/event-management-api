package com.playzone.pems.application.contrato.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerarContratoCommand {

    @NotNull
    private final Long idEventoPrivado;

    @NotNull
    private final Long idUsuarioRedactor;

    @NotBlank
    private final String contenidoTexto;
}