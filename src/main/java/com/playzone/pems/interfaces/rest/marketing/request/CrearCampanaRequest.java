package com.playzone.pems.interfaces.rest.marketing.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.Instant;

@Getter
public class CrearCampanaRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @Size(max = 300)
    private String descripcion;

    @NotNull
    private Long idPlantillaEmail;

    private Instant fechaProgramada;
}
