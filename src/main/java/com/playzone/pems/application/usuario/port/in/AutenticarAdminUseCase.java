package com.playzone.pems.application.usuario.port.in;

public interface AutenticarAdminUseCase {

    record Command(String correo, String contrasena) {}

    record Result(String token, Long idAdmin, String nombre, Long idSede) {}

    Result ejecutar(Command command);
}