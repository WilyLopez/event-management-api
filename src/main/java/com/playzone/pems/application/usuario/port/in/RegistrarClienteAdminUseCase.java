package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.RegistrarClienteAdminCommand;
import com.playzone.pems.application.usuario.dto.query.ClienteQuery;

public interface RegistrarClienteAdminUseCase {

    ClienteQuery ejecutar(RegistrarClienteAdminCommand command);
}
