package com.playzone.pems.domain.marketing.model;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TipoEmail {

    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean esSistema;
    private int     orden;
    private boolean activo;
}
