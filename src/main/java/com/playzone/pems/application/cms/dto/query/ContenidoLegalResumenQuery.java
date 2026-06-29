package com.playzone.pems.application.cms.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ContenidoLegalResumenQuery {

    private String        tipo;
    private String        etiqueta;
    private String        slug;
    private String        titulo;
    private int           version;
    private boolean       visibleFooter;
    private OffsetDateTime fechaActualizacion;
}
