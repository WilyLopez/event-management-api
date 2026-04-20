package com.playzone.pems.application.inventario.port.in;

import com.playzone.pems.application.inventario.dto.command.MovimientoInventarioCommand;
import com.playzone.pems.application.inventario.dto.query.ProductoQuery;

public interface RegistrarSalidaStockUseCase {

    ProductoQuery ejecutar(MovimientoInventarioCommand command);
}