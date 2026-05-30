package com.playzone.pems.interfaces.rest.finanzas.request;

import com.playzone.pems.domain.finanzas.model.enums.TipoMovimientoCaja;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class RegistrarMovimientoManualRequest {

    @NotNull
    private TipoMovimientoCaja tipo;

    @NotBlank
    private String concepto;

    @NotNull
    @Positive
    private BigDecimal monto;

    private String medioPago;
}
