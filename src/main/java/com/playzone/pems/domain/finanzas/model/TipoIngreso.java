package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoIngreso {
    private Long            id;
    private String          nombre;
    private String          descripcion;
    private CategoriaIngreso categoria;
    private boolean         activo;
    private LocalDateTime   fechaCreacion;
}
