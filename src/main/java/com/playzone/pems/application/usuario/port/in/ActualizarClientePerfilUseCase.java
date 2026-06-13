package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.ActualizarClientePerfilCommand;
import com.playzone.pems.domain.usuario.model.ClientePerfil;

public interface ActualizarClientePerfilUseCase {

    ClientePerfil ejecutar(Long id, ActualizarClientePerfilCommand command);
}
