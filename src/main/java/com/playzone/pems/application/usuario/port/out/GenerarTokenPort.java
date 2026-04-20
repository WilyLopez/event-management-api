package com.playzone.pems.application.usuario.port.out;

public interface GenerarTokenPort {

    String generarTokenAcceso(Long idUsuario, String correo, String rol);

    String generarTokenVerificacionCorreo(Long idCliente);

    boolean esTokenValido(String token);

    Long extraerIdUsuario(String token);
}