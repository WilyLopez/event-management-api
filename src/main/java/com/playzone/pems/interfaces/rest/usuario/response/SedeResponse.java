package com.playzone.pems.interfaces.rest.usuario.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SedeResponse {
    private Long          id;
    private String        nombre;
    private String        direccion;
    private String        ciudad;
    private String        departamento;
    private String        telefono;
    private String        correo;
    private String        ruc;
    private boolean       activo;
    private LocalDateTime fechaCreacion;
}
