package com.playzone.pems.application.calendario.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class CrearProgramacionSemanalCommand {

    private final Long      idSede;
    private final UUID      idUsuarioAdmin;
    private final LocalDate semanaInicio;
    private final LocalDate semanaFin;

    /** true cuando la crea el job automático, false cuando la crea el admin manualmente. */
    @Builder.Default
    private final boolean autoGenerada = false;
}
