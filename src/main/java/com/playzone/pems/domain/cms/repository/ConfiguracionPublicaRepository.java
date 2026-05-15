package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.ConfiguracionPublica;

import java.util.Optional;

public interface ConfiguracionPublicaRepository {

    Optional<ConfiguracionPublica> findFirst();

    ConfiguracionPublica save(ConfiguracionPublica configuracion);
}
