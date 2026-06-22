package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class GuardarPresupuestoCommand {
    private Long       idEventoPrivado;
    private String     concepto;
    private String     categoria;
    private BigDecimal montoEstimado;
    private UUID       idUsuarioRegistra;
}
