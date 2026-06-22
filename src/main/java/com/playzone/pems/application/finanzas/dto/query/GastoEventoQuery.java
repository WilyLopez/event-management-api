package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class GastoEventoQuery {
    private Long          id;
    private Long          idEventoPrivado;
    private String        descripcion;
    private BigDecimal    monto;
    private String        comprobanteUrl;
    private OffsetDateTime fechaCreacion;
}
