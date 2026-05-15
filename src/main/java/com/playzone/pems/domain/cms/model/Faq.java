package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.LocalDateTime;

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
    private Long          idUsuarioEditor;
    private LocalDateTime fechaActualizacion;
}
