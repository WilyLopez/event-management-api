package com.playzone.pems.application.marketing.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class EnvioEmailQuery {

    private Long    id;
    private Long    idCampanaEmail;
    private Long    idCliente;
    private String  destinatario;
    private String  asunto;
    private String  estado;
    private int     intentos;
    private Instant fechaEnvio;
    private String  mensajeError;
    private Instant fechaCreacion;
}
