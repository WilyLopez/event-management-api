package com.playzone.pems.interfaces.rest.promocion.request;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.enums.TipoPromocion;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CrearPromocionRequest {

    @NotNull
    private TipoPromocion tipoPromocion;

    private Long idSede;

    @NotBlank @Size(max = 150)
    private String nombre;

    @Size(max = 400)
    private String descripcion;

    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal valorDescuento;

    @Size(max = 300)
    private String condicion;

    @Min(1)
    private Integer minimoPersonas;

    private TipoDia soloTipoDia;

    @NotNull
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @NotNull
    private Boolean esAutomatica;
}