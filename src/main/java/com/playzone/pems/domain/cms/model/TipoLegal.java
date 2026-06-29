package com.playzone.pems.domain.cms.model;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TipoLegal {

    private String  codigo;
    private String  etiqueta;
    private String  slug;
    private int     orden;
    private boolean esSistema;
    private boolean requerido;
    private boolean visibleFooter;
}
