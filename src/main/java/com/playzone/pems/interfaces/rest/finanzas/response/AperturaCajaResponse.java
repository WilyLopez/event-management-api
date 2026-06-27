package com.playzone.pems.interfaces.rest.finanzas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AperturaCajaResponse {
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
}
