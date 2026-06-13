package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;

import java.math.BigDecimal;
import java.util.UUID;

public interface ConfirmarEventoPrivadoUseCase {

    EventoPrivadoQuery ejecutar(Long idEvento, BigDecimal precioTotal,
                                BigDecimal montoAdelanto,
                                UUID idUsuarioGestor,
                                String medioPago);
}
