package com.playzone.pems.application.finanzas.dto.query;

import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AperturaCajaQuery {
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
}
