package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.TipoLegal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TipoLegalQuery {

    private String  codigo;
    private String  etiqueta;
    private String  slug;
    private int     orden;
    private boolean esSistema;
    private boolean requerido;
    private boolean visibleFooter;
    private boolean yaCreado;

    public static TipoLegalQuery from(TipoLegal t, boolean yaCreado) {
        return TipoLegalQuery.builder()
                .codigo(t.getCodigo())
                .etiqueta(t.getEtiqueta())
                .slug(t.getSlug())
                .orden(t.getOrden())
                .esSistema(t.isEsSistema())
                .requerido(t.isRequerido())
                .visibleFooter(t.isVisibleFooter())
                .yaCreado(yaCreado)
                .build();
    }
}
