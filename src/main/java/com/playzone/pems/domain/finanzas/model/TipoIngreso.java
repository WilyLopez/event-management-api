package com.playzone.pems.domain.finanzas.model;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoIngreso {
    private String         codigo;
    private String         nombre;
    private String         descripcion;
    private boolean        esSistema;
    private int            orden;
    private boolean        activo;
    private OffsetDateTime  createdAt;
    private OffsetDateTime  updatedAt;
}
