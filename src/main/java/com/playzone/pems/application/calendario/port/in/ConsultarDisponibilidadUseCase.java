package com.playzone.pems.application.calendario.port.in;

import com.playzone.pems.application.calendario.dto.query.DisponibilidadQuery;

import java.time.LocalDate;
import java.util.List;

public interface ConsultarDisponibilidadUseCase {

    DisponibilidadQuery consultarPorFecha(Long idSede, LocalDate fecha);

    List<DisponibilidadQuery> consultarRango(Long idSede, LocalDate inicio, LocalDate fin);
}