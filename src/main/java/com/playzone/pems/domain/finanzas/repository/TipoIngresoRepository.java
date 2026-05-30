package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.TipoIngreso;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;

import java.util.List;
import java.util.Optional;

public interface TipoIngresoRepository {
    List<TipoIngreso> findAll();
    Optional<TipoIngreso> findById(Long id);
    Optional<TipoIngreso> findActivoByCategoria(CategoriaIngreso categoria);
    TipoIngreso save(TipoIngreso tipoIngreso);
    void desactivar(Long id);
}
