package com.playzone.pems.interfaces.rest.finanzas.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AbrirCajaRequest {

    @NotNull
    private LocalDate fecha;

    private BigDecimal saldoInicial;
    private String     observaciones;
}
