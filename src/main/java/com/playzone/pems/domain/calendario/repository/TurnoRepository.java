package com.playzone.pems.domain.calendario.repository;

import com.playzone.pems.domain.calendario.model.Turno;

import java.util.List;
import java.util.Optional;

public interface TurnoRepository {

    Optional<Turno> findById(Long id);

    Optional<Turno> findByCodigo(String codigo);

    List<Turno> findAll();
}