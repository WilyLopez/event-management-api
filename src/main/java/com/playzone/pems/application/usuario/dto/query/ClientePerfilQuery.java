package com.playzone.pems.application.usuario.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientePerfilQuery {

    private String  search;
    private Boolean esVip;
    private Boolean activo;
    private Boolean frecuente;
    private Boolean aceptaComunicaciones;
    private String  segmentoCodigo;
    private String  origen;

    @Builder.Default
    private int minVisitas = 5;
}
