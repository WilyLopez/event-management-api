package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.command.CrearTipoIngresoCommand;
import com.playzone.pems.application.finanzas.dto.query.TipoIngresoQuery;

import java.util.List;

public interface GestionarTipoIngresoUseCase {
    TipoIngresoQuery crear(CrearTipoIngresoCommand command);
    List<TipoIngresoQuery> listar();
    void desactivar(Long id);
}
