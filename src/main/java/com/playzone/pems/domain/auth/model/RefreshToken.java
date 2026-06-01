package com.playzone.pems.domain.auth.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class RefreshToken {

    private Long    id;
    private String  token;
    private Long    idUsuario;
    private String  correo;
    private String  tipoUsuario;
    private Instant fechaCreacion;
    private Instant fechaExpira;
    private boolean revocado;
    private Instant ultimoUso;

    public boolean estaExpirado() {
        return revocado || fechaExpira.isBefore(Instant.now());
    }
}
