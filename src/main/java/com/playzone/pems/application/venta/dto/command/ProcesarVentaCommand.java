package com.playzone.pems.application.venta.dto.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ProcesarVentaCommand {

    @NotNull
    private final Long idSede;

    @NotNull
    private final UUID createdBy;

    private final Long    clienteId;
    private final Long    eventoId;

    @NotBlank
    private final String tipo;

    @NotBlank
    private final String canalCodigo;

    private final LocalDate fechaVisita;
    private final String    nombreAcompanante;
    private final String    dniAcompanante;
    private final String    telefonoAcompanante;
    private final Long      promocionId;

    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private final BigDecimal efectivoRecibido;

    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private final BigDecimal vuelto;

    private final boolean actaFirmada;
    private final boolean esAnticipada;
    private final String  notas;

    @NotEmpty @Valid
    private final List<LineaVentaCommand> lineas;

    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private final BigDecimal descuento;

    @Getter
    @Builder
    public static class LineaVentaCommand {

        @NotNull @Min(1)
        private final Integer cantidad;

        @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2)
        private final BigDecimal precioUnitario;
    }
}
