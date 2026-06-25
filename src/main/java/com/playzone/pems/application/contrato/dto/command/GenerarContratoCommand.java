package com.playzone.pems.application.contrato.dto.command;

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

    private final String contenidoTexto;

    private final String plantilla;
}