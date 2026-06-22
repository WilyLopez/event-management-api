package com.playzone.pems.application.finanzas.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class RegistrarGastoEventoCommand {
    private Long       idEventoPrivado;
    private String     descripcion;
    private BigDecimal monto;
    private String     comprobanteUrl;
    private UUID       idUsuarioRegistra;
}
