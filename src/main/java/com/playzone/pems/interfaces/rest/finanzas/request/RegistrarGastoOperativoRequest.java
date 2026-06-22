package com.playzone.pems.interfaces.rest.finanzas.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RegistrarGastoOperativoRequest {

    @NotNull
    private LocalDate fecha;

    @NotBlank
    private String descripcion;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal monto;

    private String comprobanteUrl;
}
