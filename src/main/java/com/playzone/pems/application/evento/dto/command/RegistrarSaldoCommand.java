package com.playzone.pems.application.evento.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class RegistrarSaldoCommand {
    private final Long       idEvento;
    private final BigDecimal monto;
    private final String     medioPago;
    private final UUID       idUsuario;
}
