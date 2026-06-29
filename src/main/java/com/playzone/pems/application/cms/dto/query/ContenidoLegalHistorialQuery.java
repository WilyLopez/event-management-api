package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.ContenidoLegalHistorial;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ContenidoLegalHistorialQuery {

    private Long          id;
    private String        tipo;
    private String        titulo;
    private String        contenido;
    private int           version;
    private UUID          createdBy;
    private OffsetDateTime fechaCreacion;

    public static ContenidoLegalHistorialQuery from(ContenidoLegalHistorial h) {
        return ContenidoLegalHistorialQuery.builder()
                .id(h.getId())
                .tipo(h.getTipo())
                .titulo(h.getTitulo())
                .contenido(h.getContenido())
                .version(h.getVersion())
                .createdBy(h.getCreatedBy())
                .fechaCreacion(h.getCreatedAt())
                .build();
    }
}
