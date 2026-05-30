package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AperturaCaja {
    private Long       id;
    private Long       idSede;
    private LocalDate  fecha;
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinal;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private EstadoCaja estado;
    private Long       idUsuarioApertura;
    private Long       idUsuarioCierre;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private String     observaciones;
    private LocalDateTime fechaCreacion;
}
