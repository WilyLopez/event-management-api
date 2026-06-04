package com.playzone.pems.interfaces.rest.evento.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaPresencialResponse {

    private Long           idVenta;
    private Long           idSede;
    private Long           idCliente;
    private LocalDate      fechaVisita;
    private String         nombreAcompanante;
    private String         dniAcompanante;
    private BigDecimal     subtotal;
    private Long           idPromocion;
    private BigDecimal     descuento;
    private BigDecimal     total;
    private BigDecimal     efectivoRecibido;
    private BigDecimal     vuelto;
    private boolean        esAnticipada;
    private LocalDateTime  fechaCreacion;
    private List<TicketResumenResponse> tickets;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketResumenResponse {
        private Long   idReserva;
        private String numeroTicket;
        private String nombreNino;
        private int    edadNino;
        private String codigoQr;
    }
}
