package com.playzone.pems.interfaces.rest.proveedor.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProveedorResponse {
    private final Long          id;
    private final String        nombre;
    private final String        ruc;
    private final String        contactoNombre;
    private final String        contactoTelefono;
    private final String        contactoCorreo;
    private final String        tipoServicio;
    private final boolean       activo;
    private final LocalDateTime fechaCreacion;
}