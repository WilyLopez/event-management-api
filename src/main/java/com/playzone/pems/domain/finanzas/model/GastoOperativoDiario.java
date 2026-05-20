package com.playzone.pems.domain.finanzas.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GastoOperativoDiario {
    private Long          id;
    private Long          idSede;
    private LocalDate     fecha;
    private String        descripcion;
    private BigDecimal    monto;
    private String        comprobanteUrl;
    private Long          idUsuarioRegistra;
    private LocalDateTime fechaCreacion;
}
