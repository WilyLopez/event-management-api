package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.command.CrearTipoEgresoCommand;
import com.playzone.pems.application.finanzas.dto.query.TipoEgresoQuery;

import java.util.List;

public interface GestionarTipoEgresoUseCase {
    TipoEgresoQuery crear(CrearTipoEgresoCommand command);
    List<TipoEgresoQuery> listar();
    List<TipoEgresoQuery> listarPorCategoria(String categoria);
    void desactivar(Long id);
}
