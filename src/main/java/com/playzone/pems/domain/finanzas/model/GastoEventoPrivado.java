package com.playzone.pems.domain.finanzas.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GastoEventoPrivado {
    private Long          id;
    private Long          idEventoPrivado;
    private LocalDate     fechaEvento;
    private String        descripcion;
    private BigDecimal    monto;
    private String        comprobanteUrl;
    private UUID          idUsuarioRegistra;
    private OffsetDateTime fechaCreacion;
}
