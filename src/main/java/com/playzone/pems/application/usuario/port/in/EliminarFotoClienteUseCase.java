package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.query.ClienteQuery;

public interface EliminarFotoClienteUseCase {
    ClienteQuery eliminarFoto(Long idCliente);
}
