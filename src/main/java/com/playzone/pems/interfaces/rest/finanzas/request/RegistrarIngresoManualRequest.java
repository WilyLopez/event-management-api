package com.playzone.pems.interfaces.rest.finanzas.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RegistrarIngresoManualRequest {

    @NotNull
    private Long idTipoIngreso;

    @NotNull
    @Positive
    private BigDecimal monto;

    @NotNull
    private LocalDate fecha;

    private String medioPago;
    private String descripcion;
}
