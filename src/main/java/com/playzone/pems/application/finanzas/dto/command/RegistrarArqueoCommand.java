package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class RegistrarArqueoCommand {
    private Long       idAperturaCaja;
    private BigDecimal saldoContado;
    private String     observaciones;
    private UUID       realizadoPor;
}
