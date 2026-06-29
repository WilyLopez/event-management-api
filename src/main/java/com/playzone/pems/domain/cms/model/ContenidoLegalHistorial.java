package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoLegalHistorial {

    private Long          id;
    private Long          legalId;
    private String        tipo;
    private String        titulo;
    private String        contenido;
    private int           version;
    private UUID          createdBy;
    private OffsetDateTime createdAt;
}
