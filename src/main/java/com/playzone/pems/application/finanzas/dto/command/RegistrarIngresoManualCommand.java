package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class RegistrarIngresoManualCommand {
    private String     tipoIngresoCodigo;
    private Long       idSede;
    private BigDecimal monto;
    private LocalDate  fecha;
    private String     medioPago;
    private String     descripcion;
    private UUID       idUsuarioRegistra;
}
