package com.playzone.pems.application.marketing.port.in;

import com.playzone.pems.application.marketing.dto.command.CrearCampanaCommand;
import com.playzone.pems.application.marketing.dto.query.CampanaEmailQuery;

public interface CrearCampanaEmailUseCase {

    CampanaEmailQuery ejecutar(CrearCampanaCommand command, Long idUsuarioCreador);
}
