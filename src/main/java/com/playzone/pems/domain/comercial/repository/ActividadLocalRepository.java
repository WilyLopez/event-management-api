package com.playzone.pems.domain.comercial.repository;

import com.playzone.pems.domain.comercial.model.ActividadLocal;

import java.util.List;
import java.util.Optional;

public interface ActividadLocalRepository {
    Optional<ActividadLocal> findById(Long id);
    List<ActividadLocal> findAllActivas();
    List<ActividadLocal> findEspeciales();
    List<ActividadLocal> findByZona(Long idZona);
    ActividadLocal save(ActividadLocal actividad);
    void deleteById(Long id);
}
