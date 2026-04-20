package com.playzone.pems.domain.contrato.repository;

import com.playzone.pems.domain.contrato.model.Contrato;

import java.util.Optional;

public interface ContratoRepository {

    Optional<Contrato> findById(Long id);

    Optional<Contrato> findByEventoPrivado(Long idEventoPrivado);

    Contrato save(Contrato contrato);

    boolean existsByEventoPrivado(Long idEventoPrivado);
}