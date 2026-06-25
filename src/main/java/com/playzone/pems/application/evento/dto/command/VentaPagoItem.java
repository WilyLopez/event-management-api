package com.playzone.pems.application.evento.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class VentaPagoItem {
    private final String     medioPagoCodigo;
    private final BigDecimal monto;
}
