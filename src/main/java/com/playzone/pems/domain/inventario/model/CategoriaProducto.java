package com.playzone.pems.domain.inventario.model;

import lombok.*;


@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProducto {

    private Long    id;
    private String  nombre;
    private String  descripcion;
    private boolean activo;
}