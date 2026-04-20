package com.playzone.pems.application.promocion.dto.command;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.enums.TipoPromocion;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class CrearPromocionCommand {

    @NotNull
    private final TipoPromocion tipoPromocion;

    private final Long idSede;

    @NotBlank
    @Size(max = 150)
    private final String nombre;

    @Size(max = 400)
    private final String descripcion;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private final BigDecimal valorDescuento;

    @Size(max = 300)
    private final String condicion;

    @Min(1)
    private final Integer minimoPersonas;

    private final TipoDia soloTipoDia;

    @NotNull
    private final LocalDate fechaInicio;

    private final LocalDate fechaFin;

    @NotNull
    private final Boolean esAutomatica;

    @NotNull
    private final Long idUsuarioCreador;
}