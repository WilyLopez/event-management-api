package com.playzone.pems.application.usuario.port.in;

public interface CambiarPasswordMeUseCase {
    void ejecutar(String accessToken, String passwordActual, String nuevoPassword);
}
