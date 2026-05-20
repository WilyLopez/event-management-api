package com.playzone.pems.interfaces.rest.finanzas.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class GastoOperativoResponse {
    private Long          id;
    private Long          idSede;
    private LocalDate     fecha;
    private String        descripcion;
    private BigDecimal    monto;
    private String        comprobanteUrl;
    private LocalDateTime fechaCreacion;
}
