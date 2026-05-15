package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.SeccionWeb;

import java.util.List;
import java.util.Optional;

public interface SeccionWebRepository {

    Optional<SeccionWeb> findById(Long id);

    Optional<SeccionWeb> findByCodigo(String codigo);

    List<SeccionWeb> findAll();

    List<SeccionWeb> findVisibles();

    SeccionWeb save(SeccionWeb seccionWeb);

    void deleteById(Long id);

    boolean existsByCodigo(String codigo);
}
