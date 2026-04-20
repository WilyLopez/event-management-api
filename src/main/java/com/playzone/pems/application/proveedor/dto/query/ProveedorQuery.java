package com.playzone.pems.application.proveedor.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProveedorQuery {

    private final Long          id;
    private final String        nombre;
    private final String        ruc;
    private final String        contactoNombre;
    private final String        contactoTelefono;
    private final String        contactoCorreo;
    private final String        tipoServicio;
    private final String        notas;
    private final boolean       activo;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime fechaActualizacion;
}