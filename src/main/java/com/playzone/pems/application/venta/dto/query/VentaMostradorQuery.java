package com.playzone.pems.application.venta.dto.query;

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
public class VentaMostradorQuery {

    private Long          ventaId;
    private Long          sedeId;
    private LocalDate     fechaVisita;
    private BigDecimal    subtotal;
    private BigDecimal    descuento;
    private BigDecimal    total;
    private BigDecimal    efectivoRecibido;
    private BigDecimal    vuelto;
    private OffsetDateTime createdAt;
    private List<TicketMostradorQuery>     tickets;
    private List<PagoMostradorResultQuery> pagos;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketMostradorQuery {
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
    public static class PagoMostradorResultQuery {
        private Long       pagoId;
        private String     medioPago;
        private BigDecimal monto;
    }
}
