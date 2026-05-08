package com.playzone.pems.application.usuario.port.out;

public interface EnviarCorreoVerificacionPort {

    void enviarBienvenida(String destinatario, String nombreCliente);
}