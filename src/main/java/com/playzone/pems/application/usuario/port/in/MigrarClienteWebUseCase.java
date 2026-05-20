package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.MigrarClienteWebCommand;
import com.playzone.pems.application.usuario.dto.query.ClienteQuery;

public interface MigrarClienteWebUseCase {

    ClienteQuery ejecutar(MigrarClienteWebCommand command);
}
