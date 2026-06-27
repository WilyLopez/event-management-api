package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.command.RegistrarGastoEventoCommand;
import com.playzone.pems.application.finanzas.dto.query.GastoEventoQuery;

import java.time.LocalDate;
import java.util.List;

public interface GestionarGastoEventoUseCase {
    GastoEventoQuery registrar(RegistrarGastoEventoCommand command);
    List<GastoEventoQuery> listarPorEvento(Long idEvento);
    List<GastoEventoQuery> listarPorSedeYRango(Long idSede, LocalDate inicio, LocalDate fin);
    void eliminar(Long id);
}
