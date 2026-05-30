package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ConfirmarEventoRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precioTotal;

    @NotNull
    @PositiveOrZero
    private BigDecimal montoAdelanto;

    @NotBlank
    private String medioPagoAdelanto;
}
