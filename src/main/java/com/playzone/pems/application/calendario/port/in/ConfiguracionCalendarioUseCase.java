package com.playzone.pems.application.calendario.port.in;

import com.playzone.pems.domain.calendario.model.ConfiguracionCalendario;

public interface ConfiguracionCalendarioUseCase {

    ConfiguracionCalendario obtener(Long idSede);

    ConfiguracionCalendario actualizar(Long idSede, ConfiguracionCalendario config);
}
