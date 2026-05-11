package com.playzone.pems.domain.auditoria.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LogAuditoria {

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
    private final String        userAgent;
    private final String        nivel;
    private final String        resultado;
    private final LocalDateTime fechaLog;
}
