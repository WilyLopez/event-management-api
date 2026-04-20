package com.playzone.pems.application.evento.dto.command;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class SolicitarEventoPrivadoCommand {

    @NotNull
    private final Long idCliente;

    @NotNull
    private final Long idSede;

    @NotNull
    private final Long idTurno;

    @NotNull
    @Future
    private final LocalDate fechaEvento;

    @NotBlank
    @Size(max = 200)
    private final String tipoEvento;

    @Size(max = 200)
    private final String contactoAdicional;

    @Min(1) @Max(60)
    private final Integer aforoDeclarado;
}