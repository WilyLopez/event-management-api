package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.query.ResumenDiarioFinancieroQuery;
import com.playzone.pems.application.finanzas.dto.query.ResumenEventoFinancieroQuery;
import com.playzone.pems.application.finanzas.dto.query.ResumenFinancieroQuery;

import java.time.LocalDate;
import java.util.List;

public interface ConsultarResumenFinancieroUseCase {
    ResumenFinancieroQuery resumenMensual(Long idSede, int anio, int mes);
    ResumenEventoFinancieroQuery resumenEvento(Long idEvento);
    List<ResumenDiarioFinancieroQuery> resumenDiario(Long idSede, LocalDate inicio, LocalDate fin);
}
