package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.command.RegistrarUsuarioAdminCommand;
import com.playzone.pems.application.usuario.dto.response.UsuarioAdminResponse;

public interface RegistrarUsuarioAdminUseCase {
    UsuarioAdminResponse ejecutar(RegistrarUsuarioAdminCommand command);
}
