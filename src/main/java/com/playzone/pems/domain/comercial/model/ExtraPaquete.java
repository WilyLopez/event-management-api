package com.playzone.pems.domain.comercial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExtraPaquete {

    private Long    id;
    private Long    idPaquete;
    private String  nombre;
    private String  descripcion;
    private boolean activo;
    private int     orden;
}
