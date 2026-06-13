package com.playzone.pems.domain.finanzas.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GastoEventoPrivado {
    private Long          id;
    private Long          idEventoPrivado;
    private String        descripcion;
    private BigDecimal    monto;
    private String        comprobanteUrl;
    private UUID          idUsuarioRegistra;
    private OffsetDateTime fechaCreacion;
}
