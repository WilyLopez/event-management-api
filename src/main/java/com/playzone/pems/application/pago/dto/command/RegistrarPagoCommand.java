package com.playzone.pems.application.pago.dto.command;

import com.playzone.pems.domain.pago.model.enums.MedioPago;
import com.playzone.pems.domain.pago.model.enums.TipoPago;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RegistrarPagoCommand {

    @NotNull
    private final MedioPago medioPago;

    @NotNull
    private final TipoPago tipoPago;

    private final Long idReservaPublica;
    private final Long idEventoPrivado;
    private final Long idVenta;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    private final BigDecimal monto;

    @Size(max = 100)
    private final String referenciaPago;

    @NotNull
    private final Long idUsuarioRegistra;
}