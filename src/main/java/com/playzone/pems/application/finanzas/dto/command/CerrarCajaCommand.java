package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CerrarCajaCommand {
    private Long       idAperturaCaja;
    private BigDecimal saldoFinal;
    private Long       idUsuarioCierre;
    private String     observaciones;
}
