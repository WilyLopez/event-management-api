package com.playzone.pems.application.marketing.port.in;

import com.playzone.pems.application.marketing.dto.command.GuardarPlantillaCommand;
import com.playzone.pems.application.marketing.dto.query.PlantillaEmailQuery;

public interface CrearPlantillaEmailUseCase {

    PlantillaEmailQuery ejecutar(GuardarPlantillaCommand command, Long idUsuarioEditor);
}
