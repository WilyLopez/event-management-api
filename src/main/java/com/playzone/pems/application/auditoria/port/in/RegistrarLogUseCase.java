package com.playzone.pems.application.auditoria.port.in;

public interface RegistrarLogUseCase {

    record Command(
            Long   idUsuarioAdmin,
            String accion,
            String modulo,
            String entidadAfectada,
            Long   idEntidad,
            Object valorAnterior,
            Object valorNuevo,
            String descripcion,
            String ipOrigen,
            String userAgent
    ) {}

    void ejecutar(Command command);
}