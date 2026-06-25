package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PagoItemRequest {

    @NotBlank
    @Pattern(regexp = "EFECTIVO|YAPE|TRANSFERENCIA|TARJETA",
             message = "Medio de pago no valido.")
    private String medioPago;

    @NotNull
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero.")
    private BigDecimal monto;
}
