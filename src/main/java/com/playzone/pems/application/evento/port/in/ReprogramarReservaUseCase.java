package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.command.ReprogramarReservaCommand;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;

public interface ReprogramarReservaUseCase {

    ReservaPublicaQuery ejecutar(ReprogramarReservaCommand command);
}