package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ConfirmarEventoRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precioTotal;

    @NotNull
    @PositiveOrZero
    private BigDecimal montoAdelanto;

    /**
     * Multi-pago: uno o varios medios para el adelanto.
     * Si está vacío/nulo se usa el campo legacy medioPago.
     */
    @Valid
    private List<PagoItemRequest> pagosAdelanto;

    /** Legacy — compatibilidad con el frontend actual hasta Fase 6. */
    @Pattern(regexp = "EFECTIVO|YAPE|TRANSFERENCIA|TARJETA",
             message = "Medio de pago no valido.")
    private String medioPago;

    /** AL_CONTADO (default) | CUOTAS */
    @Pattern(regexp = "AL_CONTADO|CUOTAS", message = "Modalidad de pago no valida.")
    private String modalidadPago;

    /** Solo para CUOTAS: total de cuotas incluyendo el adelanto (mín 2, máx 4). */
    @Min(2) @Max(4)
    private Integer numeroCuotas;

    /** Fecha tope para completar todos los pagos. Solo para CUOTAS. */
    private LocalDate fechaLimitePago;
}
