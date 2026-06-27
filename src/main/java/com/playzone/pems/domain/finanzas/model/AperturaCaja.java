package com.playzone.pems.domain.finanzas.model;

import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

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
    private BigDecimal saldoEsperado;
    private BigDecimal diferencia;
    private EstadoCaja estado;
    private UUID       idUsuarioApertura;
    private UUID       idUsuarioCierre;
    private OffsetDateTime fechaApertura;
    private OffsetDateTime fechaCierre;
    private String     observaciones;
    private OffsetDateTime fechaCreacion;
}
