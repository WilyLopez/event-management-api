package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.CompletarPerfilClienteCommand;
import com.playzone.pems.domain.usuario.model.ClientePerfil;

public interface CompletarPerfilClienteUseCase {
    ClientePerfil ejecutar(CompletarPerfilClienteCommand command);
}
