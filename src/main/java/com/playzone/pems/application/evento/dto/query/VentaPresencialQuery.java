package com.playzone.pems.application.evento.dto.query;

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
public class VentaPresencialQuery {
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
    private List<TicketResumenQuery> tickets;
}
