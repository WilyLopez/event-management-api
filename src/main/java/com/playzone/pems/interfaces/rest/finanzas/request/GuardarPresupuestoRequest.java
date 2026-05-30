package com.playzone.pems.interfaces.rest.finanzas.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class GuardarPresupuestoRequest {

    @NotBlank
    private String concepto;

    private String categoria;

    @NotNull
    @PositiveOrZero
    private BigDecimal montoEstimado;
}
