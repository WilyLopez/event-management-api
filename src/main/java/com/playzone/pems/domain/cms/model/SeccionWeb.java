package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SeccionWeb {

    private String        codigo;
    private String        nombre;
    private String        descripcion;
    private boolean       esSistema;
    private boolean       activo;
    private int           orden;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
