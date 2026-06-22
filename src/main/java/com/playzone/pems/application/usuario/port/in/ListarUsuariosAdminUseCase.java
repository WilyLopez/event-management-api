package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.response.UsuarioAdminResponse;
import java.util.List;

public interface ListarUsuariosAdminUseCase {
    List<UsuarioAdminResponse> ejecutar();
}
