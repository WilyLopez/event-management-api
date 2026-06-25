package com.playzone.pems.application.evento.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class EventoCuotaQuery {
    private final Long          id;
    private final int           numeroCuota;
    private final BigDecimal    monto;
    private final LocalDate     fechaVencimiento;
    private final String        estado;
    private final Long          ventaId;
    private final OffsetDateTime createdAt;
}
