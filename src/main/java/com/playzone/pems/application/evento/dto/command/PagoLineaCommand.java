package com.playzone.pems.application.evento.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoLineaCommand {
    private String     metodo;
    private BigDecimal monto;
}
