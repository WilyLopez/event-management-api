package com.playzone.pems.application.evento.dto.query;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenVentaQuery {
    private BigDecimal precioUnitario;
    private int        cantidadNinos;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private TipoDia    tipoDia;
    private int        aforoDisponible;
    private int        aforoMaximo;
}
