package com.playzone.pems.application.calendario.port.in;

import com.playzone.pems.application.calendario.dto.command.CrearProgramacionSemanalCommand;
import com.playzone.pems.application.calendario.dto.query.ProgramacionSemanalDto;

import java.util.List;

public interface ProgramacionSemanalUseCase {

    ProgramacionSemanalDto crear(CrearProgramacionSemanalCommand command);

    /** Cancela una programacion verificando que pertenezca a la sede indicada. */
    void cancelar(Long idSede, Long id);

    /** Lista programaciones activas de la semana actual y futuras. */
    List<ProgramacionSemanalDto> listarFuturas(Long idSede);

    /** Llamado por el job: crea programacion para sedes sin cobertura en la semana actual. */
    void autoActivarSemanaActual();
}
