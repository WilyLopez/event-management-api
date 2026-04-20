package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;

public interface CancelarEventoPrivadoUseCase {

    EventoPrivadoQuery ejecutar(Long idEvento, String motivoCancelacion);
}