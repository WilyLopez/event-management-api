package com.playzone.pems.interfaces.rest.marketing.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class CrearCorreoMarketingRequest {

    @NotBlank(message = "El tipo de email es obligatorio")
    private String tipoEmailCodigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String nombre;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "Máximo 200 caracteres")
    private String asunto;

    @NotBlank(message = "El contenido en bloques es obligatorio")
    private String contenidoBloques;

    private String variablesPermitidas;

    private String contenidoFallback;
}
