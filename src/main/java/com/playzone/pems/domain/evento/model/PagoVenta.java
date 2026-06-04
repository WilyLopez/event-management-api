package com.playzone.pems.domain.evento.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoVenta {

    private Long       id;
    private Long       idVenta;
    private String     metodo;
    private BigDecimal monto;
}
