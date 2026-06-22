package com.playzone.pems.domain.configuracion.repository;

import com.playzone.pems.domain.configuracion.model.ConfiguracionGlobal;

import java.util.List;
import java.util.Optional;

public interface ConfiguracionGlobalRepository {

    List<ConfiguracionGlobal> findAll();

    Optional<ConfiguracionGlobal> findByClave(String clave);

    ConfiguracionGlobal save(ConfiguracionGlobal config);

    List<ConfiguracionGlobal> saveAll(List<ConfiguracionGlobal> configs);
}
