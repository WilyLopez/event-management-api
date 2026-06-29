package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.TipoLegal;

import java.util.List;
import java.util.Optional;

public interface TipoLegalRepository {

    boolean existsByCodigo(String codigo);

    Optional<TipoLegal> findByCodigo(String codigo);

    Optional<TipoLegal> findBySlug(String slug);

    List<TipoLegal> findAllOrdenado();
}
