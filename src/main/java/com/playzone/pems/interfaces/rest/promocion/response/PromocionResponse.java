package com.playzone.pems.interfaces.rest.promocion.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class PromocionResponse {
    private final Long        id;
    private final String      tipoPromocion;
    private final Long        idSede;
    private final String      nombre;
    private final String      descripcion;
    private final BigDecimal  valorDescuento;
    private final Integer     minimoPersonas;
    private final String      soloTipoDia;
    private final LocalDate   fechaInicio;
    private final LocalDate   fechaFin;
    private final boolean     activo;
    private final boolean     esAutomatica;
    private final OffsetDateTime fechaCreacion;
    private final int         prioridad;

    private final Integer    limiteUsos;
    private final Integer    limitePorCliente;
    private final BigDecimal montoMinimo;

    // CMS / display (from promocion_marketing; null if no marketing row)
    private final String  imagenUrl;
    private final String  bannerUrl;
    private final String  colorDestacado;
    private final String  textoPublicitario;
    private final String  textoBoton;
    private final String  urlBoton;
    private final boolean mostrarEnInicio;
    private final boolean mostrarEnCarrusel;
    private final boolean mostrarEnPaginaPromociones;
    private final boolean mostrarEnCheckout;
    private final boolean soloMovil;

    private final int        vecesUsado;
    private final BigDecimal montoAhorrado;
    private final int        clientesAtraidos;
}
