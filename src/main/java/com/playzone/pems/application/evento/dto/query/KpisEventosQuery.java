package com.playzone.pems.application.evento.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpisEventosQuery {
    private int solicitadas;
    private int confirmadas;
    private int completadasEsteMes;
    private int conSaldoPendiente;
}
