package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class CerrarCajaCommand {
    private Long       idAperturaCaja;
    private BigDecimal saldoFinal;
    private UUID       idUsuarioCierre;
    private String     observaciones;
}
