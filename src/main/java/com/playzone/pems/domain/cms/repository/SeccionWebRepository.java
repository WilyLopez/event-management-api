package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.SeccionWeb;

import java.util.List;
import java.util.Optional;

public interface SeccionWebRepository {

    Optional<SeccionWeb> findById(String codigo);

    Optional<SeccionWeb> findByCodigo(String codigo);

    List<SeccionWeb> findAll();

    List<SeccionWeb> findActivas();

    SeccionWeb save(SeccionWeb seccionWeb);

    void deleteById(String codigo);

    boolean existsByCodigo(String codigo);
}
