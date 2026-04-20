package com.playzone.pems.domain.calendario.repository;

import com.playzone.pems.domain.calendario.model.Feriado;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FeriadoRepository {

    Optional<Feriado> findById(Long id);

    Optional<Feriado> findByFecha(LocalDate fecha);

    List<Feriado> findByAnio(int anio);

    List<Feriado> findByFechaBetween(LocalDate inicio, LocalDate fin);

    Feriado save(Feriado feriado);

    void deleteById(Long id);

    boolean existsByFecha(LocalDate fecha);
}