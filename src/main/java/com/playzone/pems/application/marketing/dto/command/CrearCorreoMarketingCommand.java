package com.playzone.pems.application.marketing.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrearCorreoMarketingCommand {

    private String tipoEmailCodigo;
    private String nombre;
    private String asunto;
    private String contenidoBloques;
    private String variablesPermitidas;
    private String contenidoFallback;
}
