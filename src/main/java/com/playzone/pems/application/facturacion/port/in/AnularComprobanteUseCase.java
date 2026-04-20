package com.playzone.pems.application.facturacion.port.in;

import com.playzone.pems.application.facturacion.dto.command.AnularComprobanteCommand;
import com.playzone.pems.application.facturacion.dto.query.ComprobanteQuery;

public interface AnularComprobanteUseCase {

    ComprobanteQuery ejecutar(AnularComprobanteCommand command);
}