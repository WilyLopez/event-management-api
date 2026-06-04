package com.playzone.pems.application.evento.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarVentaPresencialCommand {
    private Long           idSede;
    private Long           idCliente;
    private LocalDate      fechaVisita;
    private String         nombreAcompanante;
    private String         dniAcompanante;
    private List<NinoVentaCommand>  ninos;
    private Long           idPromocion;
    private List<PagoLineaCommand>  pagos;
    private BigDecimal     efectivoRecibido;
    private boolean        actaFirmada;
}
