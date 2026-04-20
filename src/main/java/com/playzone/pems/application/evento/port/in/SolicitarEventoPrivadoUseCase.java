package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.command.SolicitarEventoPrivadoCommand;
import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;

public interface SolicitarEventoPrivadoUseCase {

    EventoPrivadoQuery ejecutar(SolicitarEventoPrivadoCommand command);
}