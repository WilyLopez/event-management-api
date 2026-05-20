package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoEgreso {
    private Long            id;
    private String          nombre;
    private String          descripcion;
    private CategoriaEgreso categoria;
    private boolean         activo;
    private Long            idUsuarioCreador;
    private LocalDateTime   fechaCreacion;
}
