package com.playzone.pems.interfaces.rest.finanzas.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RegistrarArqueoRequest {
    @NotNull
    @PositiveOrZero
    private BigDecimal saldoContado;
    private String     observaciones;
}
