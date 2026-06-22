package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.response.UsuarioAdminResponse;

public interface ObtenerUsuarioAdminUseCase {
    UsuarioAdminResponse ejecutar(Long id);
}
