package com.playzone.pems.application.evento.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResumenQuery {
    private Long   idReserva;
    private String numeroTicket;
    private String nombreNino;
    private int    edadNino;
    private String codigoQr;
}
