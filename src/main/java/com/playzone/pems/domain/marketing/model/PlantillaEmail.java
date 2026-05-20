package com.playzone.pems.domain.marketing.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaEmail {

    private Long    id;
    private Long    idTipoEmail;
    private String  tipoEmailCodigo;
    private String  tipoEmailNombre;
    private String  nombre;
    private String  asunto;
    private String  contenidoHtml;
    private String  contenidoFallback;
    private String  variablesPermitidas;
    private boolean activa;
    private Long    idUsuarioEditor;
    private Instant fechaActualizacion;
}
