package com.playzone.pems.application.finanzas.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ResumenEventoFinancieroQuery {
    private Long       idEvento;
    private String     tipoEvento;
    private String     nombreCliente;
    private LocalDate  fechaEvento;
    private BigDecimal ingresoContrato;
    private BigDecimal montoAdelanto;
    private BigDecimal totalGastosAdicionales;
    private BigDecimal totalGastos;
    private BigDecimal utilidadBruta;
}
