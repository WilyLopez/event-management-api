package com.playzone.pems.application.finanzas.port.in;

import com.playzone.pems.application.finanzas.dto.command.GuardarPresupuestoCommand;
import com.playzone.pems.application.finanzas.dto.query.PresupuestoEventoQuery;

import java.math.BigDecimal;
import java.util.List;

public interface GestionarPresupuestoEventoUseCase {
    PresupuestoEventoQuery guardar(GuardarPresupuestoCommand command);
    PresupuestoEventoQuery marcarEjecutado(Long id, BigDecimal montoReal);
    List<PresupuestoEventoQuery> listarPorEvento(Long idEventoPrivado);
    void eliminar(Long id);
}
