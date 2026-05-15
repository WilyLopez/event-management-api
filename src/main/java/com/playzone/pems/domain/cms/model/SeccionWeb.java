package com.playzone.pems.domain.cms.model;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SeccionWeb {

    private Long    id;
    private String  codigo;
    private String  nombre;
    private String  descripcion;
    private int     ordenVisualizacion;
    private boolean visible;
}
