package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RegistrarGastoEventoCommand {
    private Long       idEventoPrivado;
    private String     descripcion;
    private BigDecimal monto;
    private String     comprobanteUrl;
    private Long       idUsuarioRegistra;
}
