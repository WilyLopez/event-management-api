package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class AbrirCajaCommand {
    private Long       idSede;
    private LocalDate  fecha;
    private BigDecimal saldoInicial;
    private Long       idUsuarioApertura;
    private String     observaciones;
}
