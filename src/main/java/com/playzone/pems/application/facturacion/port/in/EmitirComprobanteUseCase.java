package com.playzone.pems.application.facturacion.port.in;

import com.playzone.pems.application.facturacion.dto.command.EmitirComprobanteCommand;
import com.playzone.pems.application.facturacion.dto.query.ComprobanteQuery;

public interface EmitirComprobanteUseCase {

    ComprobanteQuery ejecutar(EmitirComprobanteCommand command);
}