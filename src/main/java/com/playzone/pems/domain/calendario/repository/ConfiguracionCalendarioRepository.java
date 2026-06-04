package com.playzone.pems.domain.calendario.repository;

import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;

public interface ConfiguracionCalendarioRepository {

    ConfiguracionCalendario obtener(Long idSede);

    ConfiguracionCalendario save(ConfiguracionCalendario config);
}
