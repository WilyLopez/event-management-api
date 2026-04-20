package com.playzone.pems.domain.venta.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    private Long           id;
    private Long           idSede;
    private Long           idUsuario;
    private Long           idReservaPublica;
    private Long           idEventoPrivado;
    private BigDecimal     subtotal;
    private BigDecimal     descuento;
    private BigDecimal     total;
    private LocalDateTime  fechaVenta;

    public boolean totalEsCoherente() {
        BigDecimal esperado = subtotal.subtract(descuento);
        return total.compareTo(esperado) == 0;
    }

    public boolean estaVinculadaAEvento() {
        return idReservaPublica != null || idEventoPrivado != null;
    }

    public boolean tuvoDescuento() {
        return descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0;
    }
}