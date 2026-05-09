package com.playzone.pems.application.calendario.dto.command;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BloquearFechasCommand {

    @NotNull
    private final Long idSede;

    @NotNull
    @FutureOrPresent
    private final LocalDate fechaInicio;

    @NotNull
    private final LocalDate fechaFin;
    private String    tipoBloqueo;

    @NotBlank
    @Size(max = 300)
    private final String motivo;
}