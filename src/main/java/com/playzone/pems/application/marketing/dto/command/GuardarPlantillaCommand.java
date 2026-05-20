package com.playzone.pems.application.marketing.dto.command;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GuardarPlantillaCommand {

    @NotNull
    private final Long idTipoEmail;

    @NotBlank
    @Size(max = 120)
    private final String nombre;

    @NotBlank
    @Size(max = 200)
    private final String asunto;

    @NotBlank
    private final String contenidoHtml;

    private final String contenidoFallback;

    private final String variablesPermitidas;
}
