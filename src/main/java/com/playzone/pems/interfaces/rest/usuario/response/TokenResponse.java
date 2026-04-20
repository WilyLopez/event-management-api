package com.playzone.pems.interfaces.rest.usuario.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private final String token;
    private final Long   idUsuario;
    private final String nombre;
    private final Long   idSede;
    private final String rol;
}