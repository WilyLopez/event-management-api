package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class RegistroEgresoQuery {
    private Long          id;
    private String        tipoEgresoCodigo;
    private Long          idSede;
    private BigDecimal    monto;
    private LocalDate     fecha;
    private Integer       periodoAnio;
    private Integer       periodoMes;
    private String        descripcion;
    private String        comprobanteUrl;
    private boolean       esRecurrente;
    private OffsetDateTime fechaCreacion;
}
