package com.playzone.pems.interfaces.rest.finanzas.response;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RegistroEgresoResponse {
    private Long            id;
    private Long            idTipoEgreso;
    private String          nombreTipoEgreso;
    private CategoriaEgreso categoriaEgreso;
    private Long            idSede;
    private BigDecimal      monto;
    private LocalDate       fecha;
    private Integer         periodoAnio;
    private Integer         periodoMes;
    private String          descripcion;
    private String          comprobanteUrl;
    private boolean         esRecurrente;
    private LocalDateTime   fechaCreacion;
}
