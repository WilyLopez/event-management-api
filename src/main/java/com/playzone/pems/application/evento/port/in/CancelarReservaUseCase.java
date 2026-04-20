package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;

public interface CancelarReservaUseCase {

    ReservaPublicaQuery ejecutar(Long idReserva, String motivo);
}