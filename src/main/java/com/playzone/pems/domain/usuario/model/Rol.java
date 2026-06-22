package com.playzone.pems.domain.usuario.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean esSistema;
    private boolean activo;
    private int     orden;
}
