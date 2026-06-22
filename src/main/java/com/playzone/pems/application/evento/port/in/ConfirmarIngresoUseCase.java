package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;

import java.util.UUID;

public interface ConfirmarIngresoUseCase {
    ReservaPublicaQuery ejecutar(Long idReserva, UUID idUsuarioAdmin);
}
