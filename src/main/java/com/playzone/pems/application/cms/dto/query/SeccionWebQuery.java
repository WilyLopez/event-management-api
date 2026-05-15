package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.SeccionWeb;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeccionWebQuery {

    private Long    id;
    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private int     ordenVisualizacion;
    private boolean visible;

    public static SeccionWebQuery from(SeccionWeb s) {
        return SeccionWebQuery.builder()
                .id(s.getId())
                .codigo(s.getCodigo())
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .ordenVisualizacion(s.getOrdenVisualizacion())
                .visible(s.isVisible())
                .build();
    }
}
