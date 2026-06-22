package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.TipoIngreso;

import java.util.List;
import java.util.Optional;

public interface TipoIngresoRepository {
    List<TipoIngreso> findAll();
    Optional<TipoIngreso> findById(String codigo);
    TipoIngreso save(TipoIngreso tipoIngreso);
    void desactivar(String codigo);
}
