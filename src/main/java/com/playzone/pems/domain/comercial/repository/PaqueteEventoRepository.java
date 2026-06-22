package com.playzone.pems.domain.comercial.repository;

import com.playzone.pems.domain.comercial.model.PaqueteEvento;

import java.util.List;
import java.util.Optional;

public interface PaqueteEventoRepository {
    Optional<PaqueteEvento> findById(Long id);
    List<PaqueteEvento> findAllActivos();
    List<PaqueteEvento> findAll();
    PaqueteEvento save(PaqueteEvento paquete);
    void deleteById(Long id);
    boolean existsBySlug(String slug);
}
