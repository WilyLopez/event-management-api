package com.playzone.pems.application.proveedor.port.in;

import com.playzone.pems.application.proveedor.dto.command.GestionarProveedorCommand;
import com.playzone.pems.application.proveedor.dto.query.ProveedorQuery;

public interface GestionarProveedorUseCase {

    ProveedorQuery crear(GestionarProveedorCommand command);

    ProveedorQuery actualizar(Long idProveedor, GestionarProveedorCommand command);

    void desactivar(Long idProveedor);
}