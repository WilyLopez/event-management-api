package com.playzone.pems.application.inventario.port.in;

import com.playzone.pems.application.inventario.dto.query.AlertaStockQuery;

import java.util.List;

public interface ConsultarAlertasStockUseCase {

    List<AlertaStockQuery> ejecutar(Long idSede);
}