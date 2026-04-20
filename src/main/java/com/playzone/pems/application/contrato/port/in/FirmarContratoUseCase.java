package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.query.ContratoQuery;

public interface FirmarContratoUseCase {

    ContratoQuery ejecutar(Long idContrato);
}