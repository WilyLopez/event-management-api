package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoLegal {

    private Long          id;
    private String        tipo;
    private String        titulo;
    private String        contenido;
    private int           version;
    private boolean       activo;
    private UUID          idUsuarioEditor;
    private OffsetDateTime fechaActualizacion;
}
