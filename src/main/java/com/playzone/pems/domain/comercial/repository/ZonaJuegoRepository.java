package com.playzone.pems.domain.comercial.repository;

import com.playzone.pems.domain.comercial.model.ZonaJuego;

import java.util.List;
import java.util.Optional;

public interface ZonaJuegoRepository {
    Optional<ZonaJuego> findById(Long id);
    List<ZonaJuego> findAllActivas();
    List<ZonaJuego> findAll();
    ZonaJuego save(ZonaJuego zona);
    void deleteById(Long id);
    boolean existsBySlug(String slug);
}
