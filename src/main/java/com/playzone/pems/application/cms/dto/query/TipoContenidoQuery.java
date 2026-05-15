package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.TipoContenido;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoContenidoQuery {

    private Long   id;
    private String codigo;
    private String descripcion;

    public static TipoContenidoQuery from(TipoContenido t) {
        return TipoContenidoQuery.builder()
                .id(t.getId())
                .codigo(t.getCodigo())
                .descripcion(t.getDescripcion())
                .build();
    }
}
