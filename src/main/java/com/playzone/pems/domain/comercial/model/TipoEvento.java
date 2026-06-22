package com.playzone.pems.domain.comercial.model;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder(toBuilder = true)
public class TipoEvento {
    private String         codigo;
    private String         nombre;
    private String         descripcion;
    private String         icono;
    private boolean        esSistema;
    private boolean        activo;
    private int            orden;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
