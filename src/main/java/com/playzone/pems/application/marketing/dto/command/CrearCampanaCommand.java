package com.playzone.pems.application.marketing.dto.command;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class CrearCampanaCommand {

    @NotBlank
    @Size(max = 150)
    private final String nombre;

    @Size(max = 300)
    private final String descripcion;

    @NotNull
    private final Long idPlantillaEmail;

    private final Instant fechaProgramada;
}
