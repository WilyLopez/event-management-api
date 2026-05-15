package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.ContenidoLegal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContenidoLegalQuery {

    private Long          id;
    private String        tipo;
    private String        titulo;
    private String        contenido;
    private int           version;
    private boolean       activo;
    private LocalDateTime fechaActualizacion;

    public static ContenidoLegalQuery from(ContenidoLegal c) {
        return ContenidoLegalQuery.builder()
                .id(c.getId())
                .tipo(c.getTipo())
                .titulo(c.getTitulo())
                .contenido(c.getContenido())
                .version(c.getVersion())
                .activo(c.isActivo())
                .fechaActualizacion(c.getFechaActualizacion())
                .build();
    }
}
