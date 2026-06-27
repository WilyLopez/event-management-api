package com.playzone.pems.interfaces.rest.finanzas.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class GastoEventoResponse {
    private Long          id;
    private Long          idEventoPrivado;
    private LocalDate     fechaEvento;
    private String        descripcion;
    private BigDecimal    monto;
    private String        comprobanteUrl;
    private OffsetDateTime fechaCreacion;
}
