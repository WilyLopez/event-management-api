package com.playzone.pems.interfaces.rest.pago.request;

import com.playzone.pems.domain.pago.model.enums.MedioPago;
import com.playzone.pems.domain.pago.model.enums.TipoPago;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class RegistrarPagoRequest {

    @NotNull
    private MedioPago medioPago;

    @NotNull
    private TipoPago tipoPago;

    private Long idReservaPublica;
    private Long idEventoPrivado;
    private Long idVenta;

    @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2)
    private BigDecimal monto;

    @Size(max = 100)
    private String referenciaPago;
}