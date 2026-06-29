package com.playzone.pems.domain.cms.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentimientoLegal {

    private Long   id;
    private String origen;
    private Long   referenciaId;
    private String tipo;
    private int    version;
    private String ip;
}
