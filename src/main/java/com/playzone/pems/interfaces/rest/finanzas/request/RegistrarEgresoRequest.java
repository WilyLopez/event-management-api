package com.playzone.pems.interfaces.rest.finanzas.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RegistrarEgresoRequest {

    @NotNull
    private Long idTipoEgreso;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal monto;

    @NotNull
    private LocalDate fecha;

    private Integer periodoAnio;
    private Integer periodoMes;
    private String  descripcion;
    private String  comprobanteUrl;
    private boolean esRecurrente = false;
}
