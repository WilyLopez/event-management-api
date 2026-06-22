package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.RegistrarClientePerfilCommand;
import com.playzone.pems.domain.usuario.model.ClientePerfil;

public interface RegistrarClientePerfilUseCase {

    ClientePerfil ejecutar(RegistrarClientePerfilCommand command);
}
