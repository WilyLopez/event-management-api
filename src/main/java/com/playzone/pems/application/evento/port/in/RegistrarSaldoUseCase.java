package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.command.RegistrarSaldoCommand;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;

public interface RegistrarSaldoUseCase {
    EventoPrivadoQuery registrarSaldo(RegistrarSaldoCommand command);
}
