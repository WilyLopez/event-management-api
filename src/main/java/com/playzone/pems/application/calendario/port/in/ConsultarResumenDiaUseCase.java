package com.playzone.pems.application.calendario.port.in;

import com.playzone.pems.application.calendario.dto.query.ResumenDiaQuery;

import java.time.LocalDate;

public interface ConsultarResumenDiaUseCase {
    ResumenDiaQuery ejecutar(Long idSede, LocalDate fecha);
}