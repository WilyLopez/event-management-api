package com.playzone.pems.application.usuario.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClienteQuery {

    private final Long          id;
    private final String        nombre;
    private final String        correo;
    private final String        telefono;
    private final String        dni;
    private final String        ruc;
    private final String        razonSocial;
    private final boolean       esVip;
    private final int           contadorVisitas;
    private final boolean       correoVerificado;
    private final boolean       activo;
    private final LocalDateTime fechaCreacion;
}