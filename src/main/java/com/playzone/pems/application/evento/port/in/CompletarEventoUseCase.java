package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;

import java.util.UUID;

public interface CompletarEventoUseCase {
    EventoPrivadoQuery completar(Long idEvento, UUID idUsuarioGestor);
}
