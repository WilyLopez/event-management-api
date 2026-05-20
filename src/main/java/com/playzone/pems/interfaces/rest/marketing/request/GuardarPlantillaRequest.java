package com.playzone.pems.interfaces.rest.marketing.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class GuardarPlantillaRequest {

    @NotNull
    private Long idTipoEmail;

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotBlank
    @Size(max = 200)
    private String asunto;

    @NotBlank
    private String contenidoHtml;

    private String contenidoFallback;

    private String variablesPermitidas;
}
