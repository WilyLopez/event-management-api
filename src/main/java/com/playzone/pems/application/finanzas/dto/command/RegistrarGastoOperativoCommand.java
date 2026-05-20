package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class RegistrarGastoOperativoCommand {
    private Long       idSede;
    private LocalDate  fecha;
    private String     descripcion;
    private BigDecimal monto;
    private String     comprobanteUrl;
    private Long       idUsuarioRegistra;
}
