package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.Faq;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FaqQuery {

    private Long          id;
    private String        pregunta;
    private String        respuesta;
    private int           ordenVisualizacion;
    private boolean       visible;
    private LocalDateTime fechaActualizacion;

    public static FaqQuery from(Faq f) {
        return FaqQuery.builder()
                .id(f.getId())
                .pregunta(f.getPregunta())
                .respuesta(f.getRespuesta())
                .ordenVisualizacion(f.getOrdenVisualizacion())
                .visible(f.isVisible())
                .fechaActualizacion(f.getFechaActualizacion())
                .build();
    }
}
