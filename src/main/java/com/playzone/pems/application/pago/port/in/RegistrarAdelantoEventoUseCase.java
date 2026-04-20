package com.playzone.pems.application.pago.port.in;

import com.playzone.pems.application.pago.dto.command.RegistrarPagoCommand;
import com.playzone.pems.application.pago.dto.query.PagoQuery;

public interface RegistrarAdelantoEventoUseCase {

    PagoQuery ejecutar(RegistrarPagoCommand command);
}