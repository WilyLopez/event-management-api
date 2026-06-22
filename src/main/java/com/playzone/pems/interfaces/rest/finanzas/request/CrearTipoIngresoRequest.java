package com.playzone.pems.interfaces.rest.finanzas.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CrearTipoIngresoRequest {

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;

    private String descripcion;
}
