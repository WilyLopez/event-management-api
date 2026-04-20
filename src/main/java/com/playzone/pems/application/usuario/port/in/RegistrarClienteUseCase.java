package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.RegistrarClienteCommand;
import com.playzone.pems.application.usuario.dto.query.ClienteQuery;

public interface RegistrarClienteUseCase {

    ClienteQuery ejecutar(RegistrarClienteCommand command);
}