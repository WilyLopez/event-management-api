package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.ActualizarClienteCommand;
import com.playzone.pems.application.usuario.dto.query.ClienteQuery;

public interface ActualizarClienteUseCase {

    ClienteQuery ejecutar(Long idCliente, ActualizarClienteCommand command);
}