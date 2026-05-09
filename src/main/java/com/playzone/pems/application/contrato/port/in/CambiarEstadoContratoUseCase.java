package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.command.CambiarEstadoContratoCommand;
import com.playzone.pems.application.contrato.dto.query.ContratoQuery;

public interface CambiarEstadoContratoUseCase {
    ContratoQuery ejecutar(CambiarEstadoContratoCommand command);
}