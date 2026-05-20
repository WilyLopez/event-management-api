package com.playzone.pems.domain.marketing.repository;

import com.playzone.pems.domain.marketing.model.TipoEmail;

import java.util.List;
import java.util.Optional;

public interface TipoEmailRepository {

    List<TipoEmail> findAllActivos();

    Optional<TipoEmail> findById(Long id);

    Optional<TipoEmail> findByCodigo(String codigo);
}
