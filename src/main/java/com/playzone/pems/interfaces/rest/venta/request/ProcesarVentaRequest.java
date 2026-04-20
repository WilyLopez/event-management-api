package com.playzone.pems.interfaces.rest.venta.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProcesarVentaRequest {

    private Long idReservaPublica;
    private Long idEventoPrivado;

    @NotEmpty @Valid
    private List<LineaVentaRequest> lineas;

    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal descuento;

    @Getter
    @NoArgsConstructor
    public static class LineaVentaRequest {

        @NotNull
        private Long idProducto;

        @NotNull @Min(1)
        private Integer cantidad;
    }
}