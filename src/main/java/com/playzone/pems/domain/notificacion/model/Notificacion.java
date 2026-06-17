package com.playzone.pems.domain.notificacion.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {
    private Long id;
    private String tipoCodigo;
    private UUID destinatarioUsuarioId;
    private Long destinatarioClienteId;
    private String entidadTipo;
    private Long entidadId;
    private String titulo;
    private String mensaje;
    private String urlAccion;
    private String metadata;
    private boolean leida;
    private OffsetDateTime leidaAt;
    private String prioridad;
    private OffsetDateTime expiraAt;
    private OffsetDateTime createdAt;
}
