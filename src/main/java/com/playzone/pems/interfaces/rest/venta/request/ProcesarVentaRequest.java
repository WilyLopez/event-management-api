package com.playzone.pems.interfaces.rest.venta.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProcesarVentaRequest {

    private Long    clienteId;
    private Long    eventoId;

    @NotBlank
    private String tipo;

    @NotBlank
    private String canalCodigo;

    private LocalDate fechaVisita;
    private String    nombreAcompanante;
    private String    dniAcompanante;
    private String    telefonoAcompanante;
    private Long      promocionId;

    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal efectivoRecibido;

    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal vuelto;

    private boolean actaFirmada;
    private boolean esAnticipada;
    private String  notas;

    @NotEmpty @Valid
    private List<LineaVentaRequest> lineas;

    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal descuento;

    @Getter
    @NoArgsConstructor
    public static class LineaVentaRequest {

        @NotNull @Min(1)
        private Integer cantidad;

        @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2)
        private BigDecimal precioUnitario;
    }
}
