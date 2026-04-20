package com.playzone.pems.application.venta.port.in;

import com.playzone.pems.application.venta.dto.command.ProcesarVentaCommand;
import com.playzone.pems.application.venta.dto.query.VentaQuery;

public interface ProcesarVentaUseCase {

    VentaQuery ejecutar(ProcesarVentaCommand command);
}