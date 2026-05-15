package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.TipoContenido;

import java.util.List;
import java.util.Optional;

public interface TipoContenidoRepository {

    List<TipoContenido> findAll();

    Optional<TipoContenido> findByCodigo(String codigo);
}
