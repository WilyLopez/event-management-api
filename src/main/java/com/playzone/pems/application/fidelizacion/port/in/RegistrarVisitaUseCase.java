package com.playzone.pems.application.fidelizacion.port.in;

import com.playzone.pems.application.fidelizacion.dto.query.HistorialFidelizacionQuery;

public interface RegistrarVisitaUseCase {

    HistorialFidelizacionQuery registrarVisita(Long idReservaPublica);
}