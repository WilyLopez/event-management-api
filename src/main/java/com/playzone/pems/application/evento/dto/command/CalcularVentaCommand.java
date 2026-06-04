package com.playzone.pems.application.evento.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalcularVentaCommand {
    private Long           idSede;
    private LocalDate      fechaVisita;
    private List<NinoVentaCommand> ninos;
    private Long           idPromocion;
}
