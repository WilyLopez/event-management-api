package com.playzone.pems.domain.notificacion.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaNotificacion {
    private Long id;
    private UUID usuarioId;
    private Long clienteId;
    private String tipoCodigo;
    private List<String> canales;
    private boolean esActiva;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
