package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReprogramarReservaRequest {

    @NotNull @Future
    private LocalDate nuevaFechaEvento;
}