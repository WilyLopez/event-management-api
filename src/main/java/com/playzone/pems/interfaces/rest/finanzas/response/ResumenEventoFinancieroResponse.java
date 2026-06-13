package com.playzone.pems.interfaces.rest.finanzas.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ResumenEventoFinancieroResponse {
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
