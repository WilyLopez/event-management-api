package com.playzone.pems.interfaces.rest.inventario.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductoResponse {
    private final Long          id;
    private final Long          idSede;
    private final String        categoria;
    private final String        nombre;
    private final String        descripcion;
    private final BigDecimal    precio;
    private final int           stockActual;
    private final int           stockMinimo;
    private final String        unidadMedida;
    private final boolean       activo;
    private final boolean       enAlertaDeStock;
    private final LocalDateTime fechaActualizacion;
}