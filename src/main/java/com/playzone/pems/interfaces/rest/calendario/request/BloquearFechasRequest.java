package com.playzone.pems.interfaces.rest.calendario.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class BloquearFechasRequest {

    @NotNull @FutureOrPresent
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    @NotBlank @Size(max = 300)
    private String motivo;
}