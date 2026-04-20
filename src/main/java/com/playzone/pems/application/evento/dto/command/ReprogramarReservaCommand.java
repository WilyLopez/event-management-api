package com.playzone.pems.application.evento.dto.command;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReprogramarReservaCommand {

    @NotNull
    private final Long idReservaOriginal;

    @NotNull
    @Future
    private final LocalDate nuevaFechaEvento;
}