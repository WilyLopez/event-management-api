package com.playzone.pems.application.promocion.dto.command;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.enums.TipoPromocion;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class CrearPromocionCommand {

    private final Long id;

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

    @Min(1)
    private final Integer minimoPersonas;

    private final TipoDia soloTipoDia;

    @NotNull
    private final LocalDate fechaInicio;

    private final LocalDate fechaFin;

    @NotNull
    private final Boolean esAutomatica;

    private final UUID idUsuarioCreador;

    private final Integer prioridad;

    private final Integer    limiteUsos;
    private final Integer    limitePorCliente;
    private final BigDecimal montoMinimo;

    // CMS / display
    private final String  imagenUrl;
    private final String  bannerUrl;
    private final String  colorDestacado;
    private final String  textoPublicitario;
    private final String  textoBoton;
    private final String  urlBoton;
    private final Boolean mostrarEnInicio;
    private final Boolean mostrarEnCarrusel;
    private final Boolean mostrarEnPaginaPromociones;
    private final Boolean mostrarEnCheckout;
    private final Boolean soloMovil;
}
