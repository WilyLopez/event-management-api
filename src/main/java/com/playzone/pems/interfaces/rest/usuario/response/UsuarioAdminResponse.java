package com.playzone.pems.interfaces.rest.usuario.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UsuarioAdminResponse {
    private final Long          id;
    private final Long          idSede;
    private final String        nombre;
    private final String        correo;
    private final String        rol;
    private final String        telefono;
    private final String        fotoPerfilUrl;
    private final boolean       activo;
    private final boolean       debeCambiarContrasena;
    private final int           intentosFallidos;
    private final LocalDateTime bloqueadoHasta;
    private final LocalDateTime ultimoAcceso;
    private final LocalDateTime fechaCreacion;
}
