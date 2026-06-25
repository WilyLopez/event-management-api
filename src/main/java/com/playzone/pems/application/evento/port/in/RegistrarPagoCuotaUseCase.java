package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.command.RegistrarPagoCuotaCommand;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;

public interface RegistrarPagoCuotaUseCase {
    EventoPrivadoQuery ejecutar(RegistrarPagoCuotaCommand command);
}
