package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class RegistrarIngresoManualCommand {
    private Long       idTipoIngreso;
    private Long       idSede;
    private BigDecimal monto;
    private LocalDate  fecha;
    private String     medioPago;
    private String     descripcion;
    private Long       idUsuarioRegistra;
}
