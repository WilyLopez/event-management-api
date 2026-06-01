package com.playzone.pems.application.dashboard.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgendaReservaQuery {
    private final String numeroTicket;
    private final String nombreNino;
    private final int    edadNino;
    private final String estado;
}
