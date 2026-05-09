package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.query.ContratoQuery;

public interface ObtenerContratoUseCase {
    ContratoQuery porId(Long id);
    ContratoQuery porEvento(Long idEvento);
}