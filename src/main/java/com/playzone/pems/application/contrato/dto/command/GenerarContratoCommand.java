package com.playzone.pems.application.contrato.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GenerarContratoCommand {

    @NotNull
    private final Long idEventoPrivado;

    private final UUID idUsuarioRedactor;

    @NotBlank
    private final String contenidoTexto;

    @NotBlank
    private final String plantilla;
}