package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.TipoContenido;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoContenidoQuery {

    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean esSistema;
    private boolean activo;
    private int     orden;

    public static TipoContenidoQuery from(TipoContenido t) {
        return TipoContenidoQuery.builder()
                .codigo(t.getCodigo())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .esSistema(t.isEsSistema())
                .activo(t.isActivo())
                .orden(t.getOrden())
                .build();
    }
}
