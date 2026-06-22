package com.playzone.pems.application.venta.dto.query;

import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class VentaDetalleQuery {

    private final Long id;
    private final Long idSede;
    private final Long clienteId;
    private final Long eventoId;
    private final String tipo;
    private final String canalCodigo;
    private final LocalDate fechaVisita;
    private final BigDecimal subtotal;
    private final BigDecimal descuento;
    private final BigDecimal total;
    private final String nombreAcompanante;
    private final String dniAcompanante;
    private final String telefonoAcompanante;
    private final String nombreCliente;
    private final String notas;
    private final boolean impreso;
    private final boolean enviadoCorreo;
    private final boolean descargado;
    private final BigDecimal efectivoRecibido;
    private final BigDecimal vuelto;
    private final OffsetDateTime createdAt;
    private final List<ReservaPublicaQuery> tickets;
    private final List<PagoDetalleQuery> pagos;
    private final BigDecimal totalPagado;

    @Getter
    @Builder
    public static class PagoDetalleQuery {
        private final Long id;
        private final String medioPago;
        private final BigDecimal monto;
        private final String referencia;
        private final boolean esValidado;
    }
}
