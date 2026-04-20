package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.command.GenerarContratoCommand;
import com.playzone.pems.application.contrato.dto.query.ContratoQuery;

public interface GenerarContratoUseCase {

    ContratoQuery ejecutar(GenerarContratoCommand command);
}