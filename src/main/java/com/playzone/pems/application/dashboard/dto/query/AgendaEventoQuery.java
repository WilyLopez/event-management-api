package com.playzone.pems.application.dashboard.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgendaEventoQuery {
    private final Long   id;
    private final String tipoEvento;
    private final String nombreCliente;
    private final String turno;
    private final String estado;
}
