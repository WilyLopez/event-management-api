package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Faq {

    private Long          id;
    private String        pregunta;
    private String        respuesta;
    private int           ordenVisualizacion;
    private boolean       visible;
    private UUID          idUsuarioEditor;
    private OffsetDateTime fechaActualizacion;
}
