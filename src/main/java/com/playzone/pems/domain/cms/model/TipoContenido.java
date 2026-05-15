package com.playzone.pems.domain.cms.model;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TipoContenido {

    private Long   id;
    private String codigo;
    private String descripcion;
}
