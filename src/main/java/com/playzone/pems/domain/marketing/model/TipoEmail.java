package com.playzone.pems.domain.marketing.model;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TipoEmail {

    private Long    id;
    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private boolean activo;
}
