package com.playzone.pems.domain.comercial.repository;

import com.playzone.pems.domain.comercial.model.NovedadLocal;

import java.util.List;
import java.util.Optional;

public interface NovedadLocalRepository {
    Optional<NovedadLocal> findById(Long id);
    List<NovedadLocal> findAllActivas();
    List<NovedadLocal> findVisiblesHome();
    NovedadLocal save(NovedadLocal novedad);
    void deleteById(Long id);
}
