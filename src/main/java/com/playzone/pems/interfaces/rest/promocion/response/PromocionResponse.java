package com.playzone.pems.interfaces.rest.promocion.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PromocionResponse {
    private final Long        id;
    private final String      tipoPromocion;
    private final Long        idSede;
    private final String      nombre;
    private final String      descripcion;
    private final BigDecimal  valorDescuento;
    private final String      condicion;
    private final Integer     minimoPersonas;
    private final String      soloTipoDia;
    private final LocalDate   fechaInicio;
    private final LocalDate   fechaFin;
    private final boolean     activo;
    private final boolean     esAutomatica;
    private final LocalDateTime fechaCreacion;
}