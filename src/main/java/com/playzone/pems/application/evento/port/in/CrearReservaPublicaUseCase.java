package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.command.CrearReservaPublicaCommand;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;

public interface CrearReservaPublicaUseCase {

    ReservaPublicaQuery ejecutar(CrearReservaPublicaCommand command);
}