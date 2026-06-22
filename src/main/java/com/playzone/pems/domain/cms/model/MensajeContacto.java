package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MensajeContacto {
    private Long           id;
    private String         nombre;
    private String         correo;
    private String         telefono;
    private String         asunto;
    private String         mensaje;
    private String         estado; // PENDIENTE, LEIDO, RESPONDIDO, SPAM, ARCHIVADO
    private String         respuesta;
    private UUID           respondidoPor;
    private OffsetDateTime respondidoAt;
    private String         ipOrigen;
    private String         userAgent;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
