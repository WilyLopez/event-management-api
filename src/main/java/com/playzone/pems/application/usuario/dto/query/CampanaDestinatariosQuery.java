package com.playzone.pems.application.usuario.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CampanaDestinatariosQuery {

    private Boolean soloVip;
    private Boolean soloFrecuentes;
    private Boolean soloNuevos;
    private Boolean soloInactivos;
    private Boolean soloCorporativos;
    private Boolean soloPresenciales;

    @Builder.Default
    private int minVisitas = 5;
}
