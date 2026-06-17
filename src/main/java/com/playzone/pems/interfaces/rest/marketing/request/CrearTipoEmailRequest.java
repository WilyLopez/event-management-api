package com.playzone.pems.interfaces.rest.marketing.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class CrearTipoEmailRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Solo mayúsculas, números y guión bajo (ej: BIENVENIDA)")
    private String codigo;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nombre;

    @Size(max = 300)
    private String descripcion;
}
