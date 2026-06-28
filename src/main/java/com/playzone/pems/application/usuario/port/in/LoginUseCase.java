package com.playzone.pems.application.usuario.port.in;

import java.util.Map;

public interface LoginUseCase {
    Map<String, Object> ejecutar(String email, String password, String ipOrigen, String userAgent);
}
