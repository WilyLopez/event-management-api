package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.command.ActualizarContratoCommand;
import com.playzone.pems.application.contrato.dto.query.ContratoQuery;

public interface ActualizarContratoUseCase {
    ContratoQuery ejecutar(ActualizarContratoCommand command);
}