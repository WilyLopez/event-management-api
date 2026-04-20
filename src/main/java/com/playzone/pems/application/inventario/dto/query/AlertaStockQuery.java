package com.playzone.pems.application.inventario.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AlertaStockQuery {

    private final Long          idProducto;
    private final Long          idSede;
    private final String        nombre;
    private final String        categoria;
    private final int           stockActual;
    private final int           stockMinimo;
    private final int           unidadesParaReponer;
    private final LocalDateTime fechaActualizacion;
}