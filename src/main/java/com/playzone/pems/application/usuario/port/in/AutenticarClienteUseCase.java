package com.playzone.pems.application.usuario.port.in;

public interface AutenticarClienteUseCase {

    record Command(String correo, String contrasena) {}

    record Result(String token, Long idCliente, String nombre) {}

    Result ejecutar(Command command);
}