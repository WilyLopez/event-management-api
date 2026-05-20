package com.playzone.pems.application.marketing.port.in;

import com.playzone.pems.application.marketing.dto.command.FiltroDestinatariosCommand;

public interface EnviarCampanaUseCase {

    void ejecutar(Long idCampana, FiltroDestinatariosCommand filtro);
}
