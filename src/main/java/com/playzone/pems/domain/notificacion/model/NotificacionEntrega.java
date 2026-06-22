package com.playzone.pems.domain.notificacion.model;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionEntrega {
    private Long id;
    private Long notificacionId;
    private String canal;
    private String estado;
    private int intentos;
    private OffsetDateTime enviadoAt;
    private String mensajeError;
    private String proveedorId;
    private String metadata;
    private OffsetDateTime createdAt;
}
