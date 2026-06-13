package com.playzone.pems.interfaces.rest.venta.response;

import com.playzone.pems.application.venta.dto.query.VentaMostradorQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaMostradorResponse {

    private Long          ventaId;
    private Long          sedeId;
    private LocalDate     fechaVisita;
    private BigDecimal    subtotal;
    private BigDecimal    descuento;
    private BigDecimal    total;
    private BigDecimal    efectivoRecibido;
    private BigDecimal    vuelto;
    private OffsetDateTime createdAt;
    private List<TicketResponse>     tickets;
    private List<PagoResponse>       pagos;

    public static VentaMostradorResponse from(VentaMostradorQuery q) {
        List<TicketResponse> tickets = q.getTickets().stream()
                .map(t -> TicketResponse.builder()
                        .reservaId(t.getReservaId())
                        .numeroTicket(t.getNumeroTicket())
                        .codigoQr(t.getCodigoQr())
                        .nombreNino(t.getNombreNino())
                        .edadNino(t.getEdadNino())
                        .build())
                .toList();

        List<PagoResponse> pagos = q.getPagos().stream()
                .map(p -> PagoResponse.builder()
                        .pagoId(p.getPagoId())
                        .medioPago(p.getMedioPago())
                        .monto(p.getMonto())
                        .build())
                .toList();

        return VentaMostradorResponse.builder()
                .ventaId(q.getVentaId())
                .sedeId(q.getSedeId())
                .fechaVisita(q.getFechaVisita())
                .subtotal(q.getSubtotal())
                .descuento(q.getDescuento())
                .total(q.getTotal())
                .efectivoRecibido(q.getEfectivoRecibido())
                .vuelto(q.getVuelto())
                .createdAt(q.getCreatedAt())
                .tickets(tickets)
                .pagos(pagos)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketResponse {
        private Long   reservaId;
        private String numeroTicket;
        private String codigoQr;
        private String nombreNino;
        private int    edadNino;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagoResponse {
        private Long       pagoId;
        private String     medioPago;
        private BigDecimal monto;
    }
}
