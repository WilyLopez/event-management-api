package com.playzone.pems.application.venta.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoMostradorCommand {
    private String     medioPago;
    private BigDecimal monto;
    private String     referencia;
}
