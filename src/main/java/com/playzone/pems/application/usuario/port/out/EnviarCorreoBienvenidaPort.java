package com.playzone.pems.application.usuario.port.out;

/**
 * Puerto para el envío de correos de bienvenida a nuevos usuarios.
 */
public interface EnviarCorreoBienvenidaPort {

    /**
     * Envía un correo con las credenciales temporales al usuario.
     *
     * @param email            Correo del usuario
     * @param nombre           Nombre completo
     * @param passwordTemporal Contraseña generada
     * @param rol              Rol asignado
     */
    void enviarCredencialesUsuario(
            String correo,
            String nombre,
            String password,
            String rolLabel,
            String sedeNombre
    );
}
