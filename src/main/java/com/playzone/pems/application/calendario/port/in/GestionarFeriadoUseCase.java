package com.playzone.pems.application.calendario.port.in;

import com.playzone.pems.domain.calendario.model.Feriado;
import com.playzone.pems.domain.calendario.model.enums.TipoFeriado;

import java.time.LocalDate;

public interface GestionarFeriadoUseCase {

    record CrearCommand(TipoFeriado tipo, LocalDate fecha, String descripcion, Long idUsuario) {}

    Feriado crear(CrearCommand command);

    void eliminar(Long idFeriado);
}