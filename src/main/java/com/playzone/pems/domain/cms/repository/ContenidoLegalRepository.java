package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.ContenidoLegal;

import java.util.List;
import java.util.Optional;

public interface ContenidoLegalRepository {

    Optional<ContenidoLegal> findById(Long id);

    Optional<ContenidoLegal> findActivoByTipo(String tipo);

    List<ContenidoLegal> findAll();

    ContenidoLegal save(ContenidoLegal contenidoLegal);
}
