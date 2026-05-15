package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoLegal {

    private Long          id;
    private String        tipo;
    private String        titulo;
    private String        contenido;
    private int           version;
    private boolean       activo;
    private Long          idUsuarioEditor;
    private LocalDateTime fechaActualizacion;
}
