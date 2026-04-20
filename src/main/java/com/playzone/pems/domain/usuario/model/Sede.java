package com.playzone.pems.domain.usuario.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Sede {

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

    public boolean tieneRucConfigurado() {
        return ruc != null && !ruc.isBlank();
    }

    public String etiqueta() {
        return nombre + " (" + ciudad + ")";
    }
}