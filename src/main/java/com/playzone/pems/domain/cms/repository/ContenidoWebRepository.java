package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.ContenidoWeb;

import java.util.List;
import java.util.Optional;

public interface ContenidoWebRepository {

    Optional<ContenidoWeb> findById(Long id);

    Optional<ContenidoWeb> findBySeccionAndClave(Long idSeccion, String clave);

    List<ContenidoWeb> findActivosBySeccion(Long idSeccion);

    List<ContenidoWeb> findAllActivos();

    ContenidoWeb save(ContenidoWeb contenido);

    boolean existsBySeccionAndClave(Long idSeccion, String clave);
}