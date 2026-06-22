package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.RegistrarClientePublicoCommand;
import com.playzone.pems.domain.usuario.model.ClientePerfil;

public interface RegistrarClientePublicoUseCase {
    ClientePerfil ejecutar(RegistrarClientePublicoCommand command);
}
