package com.playzone.pems.domain.pago.model;

import com.playzone.pems.domain.pago.model.enums.MedioPago;
import com.playzone.pems.domain.pago.model.enums.TipoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    private Long           id;
    private MedioPago      medioPago;
    private Long           idReservaPublica;
    private Long           idEventoPrivado;
    private Long           idVenta;
    private BigDecimal     monto;
    private String         referenciaPago;
    private boolean        esParcial;
    private TipoPago       tipoPago;
    private Long           idUsuarioRegistra;
    private LocalDateTime  fechaPago;

    public boolean contextoEsValido() {
        int definidos = 0;
        if (idReservaPublica != null) definidos++;
        if (idEventoPrivado  != null) definidos++;
        if (idVenta          != null) definidos++;
        return definidos == 1;
    }

    public boolean esDeReservaPublica() {
        return idReservaPublica != null;
    }

    public boolean esDeEventoPrivado() {
        return idEventoPrivado != null;
    }

    public boolean esDeVenta() {
        return idVenta != null;
    }

    public boolean requiereReferencia() {
        return medioPago.debeRegistrarReferencia();
    }
}