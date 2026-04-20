package com.playzone.pems.application.venta.dto.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ProcesarVentaCommand {

    @NotNull
    private final Long idSede;

    @NotNull
    private final Long idUsuario;

    private final Long idReservaPublica;
    private final Long idEventoPrivado;

    @NotEmpty
    @Valid
    private final List<LineaVentaCommand> lineas;

    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private final BigDecimal descuento;

    @Getter
    @Builder
    public static class LineaVentaCommand {

        @NotNull
        private final Long idProducto;

        @NotNull
        @Min(1)
        private final Integer cantidad;
    }
}