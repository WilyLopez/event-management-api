package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.response.UsuarioAdminResponse;

public interface ActualizarUsuarioAdminUseCase {
    UsuarioAdminResponse ejecutar(Long id, String nombre, String telefono);
}
