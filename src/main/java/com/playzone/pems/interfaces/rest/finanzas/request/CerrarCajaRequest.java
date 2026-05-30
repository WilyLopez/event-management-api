package com.playzone.pems.interfaces.rest.finanzas.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CerrarCajaRequest {

    private BigDecimal saldoFinal;
    private String     observaciones;
}
