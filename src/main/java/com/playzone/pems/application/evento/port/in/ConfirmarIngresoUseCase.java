package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;

public interface ConfirmarIngresoUseCase {
    ReservaPublicaQuery ejecutar(Long idReserva, Long idUsuarioAdmin);
}