package com.playzone.pems.application.usuario.port.out;

public interface EnviarCorreoVerificacionPort {

    void enviarVerificacion(String destinatario, String nombreCliente, String urlVerificacion);
}