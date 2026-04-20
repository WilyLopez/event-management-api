package com.playzone.pems.application.calendario.port.in;

import com.playzone.pems.application.calendario.dto.command.ConfigurarTarifaCommand;
import com.playzone.pems.domain.calendario.model.Tarifa;

public interface ConfigurarTarifaUseCase {

    Tarifa ejecutar(ConfigurarTarifaCommand command);
}