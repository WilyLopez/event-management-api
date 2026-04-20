package com.playzone.pems.domain.calendario.repository;

import com.playzone.pems.domain.calendario.model.DisponibilidadDiaria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadDiariaRepository {

    Optional<DisponibilidadDiaria> findBySedeAndFecha(Long idSede, LocalDate fecha);

    List<DisponibilidadDiaria> findBySedeAndFechasBetween(
            Long idSede, LocalDate inicio, LocalDate fin);

    DisponibilidadDiaria save(DisponibilidadDiaria disponibilidad);

    void incrementarAforo(Long idSede, LocalDate fecha);

    void decrementarAforo(Long idSede, LocalDate fecha);

    void bloquearAccesoPublico(Long idSede, LocalDate fecha);

    void bloquearTurno(Long idSede, LocalDate fecha, String codigoTurno);
}