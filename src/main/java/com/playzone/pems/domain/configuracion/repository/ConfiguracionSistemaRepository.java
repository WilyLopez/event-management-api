package com.playzone.pems.domain.configuracion.repository;

import com.playzone.pems.domain.configuracion.model.ConfiguracionSistema;

import java.util.List;
import java.util.Optional;

public interface ConfiguracionSistemaRepository {

    List<ConfiguracionSistema> findAll();

    Optional<ConfiguracionSistema> findByClave(String clave);

    ConfiguracionSistema save(ConfiguracionSistema config);

    List<ConfiguracionSistema> saveAll(List<ConfiguracionSistema> configs);
}
