package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.response.UsuarioAdminResponse;

public interface CambiarRolUsuarioAdminUseCase {
    UsuarioAdminResponse ejecutar(Long id, String nuevoRol, java.util.UUID solicitanteId);
}
