package com.playzone.pems.domain.usuario.repository;

import com.playzone.pems.domain.usuario.model.Sede;

import java.util.List;
import java.util.Optional;

public interface SedeRepository {

    Optional<Sede> findById(Long id);

    List<Sede> findAllActivas();

    Sede save(Sede sede);

    boolean existsByRuc(String ruc);
}