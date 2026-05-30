package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GuardarPresupuestoCommand {
    private Long       idEventoPrivado;
    private String     concepto;
    private String     categoria;
    private BigDecimal montoEstimado;
    private Long       idUsuarioRegistra;
}
