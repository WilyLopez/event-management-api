package com.playzone.pems.application.calendario.port.out;

import java.time.LocalDate;

public interface ActualizarDisponibilidadPort {

    void recalcularDia(Long idSede, LocalDate fecha);
}