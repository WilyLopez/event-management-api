package com.playzone.pems.application.marketing.port.in;

import com.playzone.pems.application.marketing.dto.command.CrearCampanaCommand;
import com.playzone.pems.application.marketing.dto.query.CampanaEmailQuery;

import java.util.UUID;

public interface CrearCampanaEmailUseCase {

    CampanaEmailQuery ejecutar(CrearCampanaCommand command, UUID idUsuarioCreador);
}
