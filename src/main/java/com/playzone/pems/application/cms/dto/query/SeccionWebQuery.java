package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.SeccionWeb;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeccionWebQuery {

    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean esSistema;
    private boolean activo;
    private int     orden;

    public static SeccionWebQuery from(SeccionWeb s) {
        return SeccionWebQuery.builder()
                .codigo(s.getCodigo())
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .esSistema(s.isEsSistema())
                .activo(s.isActivo())
                .orden(s.getOrden())
                .build();
    }
}
