package com.playzone.pems.application.inventario.port.out;

import com.playzone.pems.application.inventario.dto.query.AlertaStockQuery;

import java.util.List;

public interface EnviarAlertaStockBajoPort {

    void notificarAdministradores(Long idSede, List<AlertaStockQuery> productos);
}