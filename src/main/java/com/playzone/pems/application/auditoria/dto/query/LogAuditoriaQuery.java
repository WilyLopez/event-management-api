package com.playzone.pems.application.auditoria.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LogAuditoriaQuery {

    private final Long          id;
    private final Long          idUsuarioAdmin;
    private final String        nombreUsuario;
    private final String        accion;
    private final String        modulo;
    private final String        entidadAfectada;
    private final Long          idEntidad;
    private final String        valorAnterior;
    private final String        valorNuevo;
    private final String        descripcion;
    private final String        ipOrigen;
    private final LocalDateTime timestamp;
}