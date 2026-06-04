package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class RegistrarVentaPresencialRequest {

    private Long      idCliente;

    @NotNull(message = "La fecha de visita es obligatoria.")
    private LocalDate fechaVisita;

    @NotBlank(message = "El nombre del acompanante es obligatorio.")
    @Size(min = 2, max = 150)
    private String    nombreAcompanante;

    @NotBlank(message = "El DNI del acompanante es obligatorio.")
    @Pattern(regexp = "\\d{7,15}", message = "DNI invalido.")
    private String    dniAcompanante;

    @NotEmpty(message = "Debe registrar al menos un nino.")
    @Valid
    private List<NinoVentaRequest> ninos;

    private Long      idPromocion;

    @NotEmpty(message = "Debe registrar al menos un metodo de pago.")
    @Valid
    private List<PagoLineaRequest> pagos;

    private BigDecimal efectivoRecibido;

    @AssertTrue(message = "Debe confirmar la firma del Acta de Responsabilidad.")
    private boolean actaFirmada;

    @Getter
    @NoArgsConstructor
    public static class NinoVentaRequest {
        @NotBlank(message = "El nombre del nino es obligatorio.")
        @Size(min = 2, max = 120)
        private String nombre;

        @Min(0) @Max(17)
        private int    edad;
    }

    @Getter
    @NoArgsConstructor
    public static class PagoLineaRequest {
        @NotBlank
        @Pattern(regexp = "EFECTIVO|YAPE|TARJETA", message = "Metodo invalido.")
        private String     metodo;

        @NotNull
        @DecimalMin(value = "0.01", message = "El monto debe ser positivo.")
        private BigDecimal monto;
    }
}
