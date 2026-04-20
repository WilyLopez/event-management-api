package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.domain.usuario.model.UsuarioAdmin;

public interface GestionarUsuarioAdminUseCase {

    record CrearCommand(Long idSede, String nombre, String correo, String contrasena) {}

    UsuarioAdmin crear(CrearCommand command);

    void desactivar(Long idAdmin);

    void activar(Long idAdmin);
}