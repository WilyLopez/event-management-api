package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.TipoEgreso;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;

import java.util.List;
import java.util.Optional;

public interface TipoEgresoRepository {
    Optional<TipoEgreso> findById(Long id);
    List<TipoEgreso> findAllActivos();
    List<TipoEgreso> findByCategoria(CategoriaEgreso categoria);
    TipoEgreso save(TipoEgreso tipo);
    void desactivar(Long id);
}
