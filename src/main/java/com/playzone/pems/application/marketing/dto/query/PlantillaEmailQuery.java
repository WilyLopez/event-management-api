package com.playzone.pems.application.marketing.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class PlantillaEmailQuery {

    private Long    id;
    private String  tipoEmailCodigo;
    private String  tipoEmailNombre;
    private String  nombre;
    private String  asunto;
    private String  contenidoHtml;
    private String  contenidoFallback;
    private String  variablesPermitidas;
    private boolean activa;
    private UUID    createdBy;
    private UUID    updatedBy;
    private Instant fechaActualizacion;
}
