package com.playzone.pems.application.finanzas.dto.query;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DesgloseTipoEgresoQuery {
    private String          nombreTipo;
    private CategoriaEgreso categoria;
    private BigDecimal      totalMonto;
}
