package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.domain.usuario.model.UsuarioAdmin;

import java.util.List;

public interface GestionarUsuarioAdminUseCase {

    record CrearCommand(Long idSede, String nombre, String correo, String contrasena,
                        String rol, String telefono) {}

    record ActualizarPerfilCommand(String nombre, String telefono) {}

    record CambiarContrasenaCommand(String contrasenaActual, String contrasenaNueva) {}

    List<UsuarioAdmin> listar();

    UsuarioAdmin obtener(Long idAdmin);

    UsuarioAdmin crear(CrearCommand command);

    UsuarioAdmin actualizarPerfil(Long idAdmin, ActualizarPerfilCommand command);

    void cambiarContrasena(Long idAdmin, CambiarContrasenaCommand command);

    void desactivar(Long idAdmin);

    void activar(Long idAdmin);
}
