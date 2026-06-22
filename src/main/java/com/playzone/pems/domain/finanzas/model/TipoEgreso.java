package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoEgreso {
    private String          codigo;
    private String          nombre;
    private String          descripcion;
    private CategoriaEgreso categoria;
    private boolean         esSistema;
    private int             orden;
    private boolean         activo;
    private OffsetDateTime   createdAt;
    private OffsetDateTime   updatedAt;
}
