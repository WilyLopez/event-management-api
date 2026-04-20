package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.command.EditarContenidoCommand;
import com.playzone.pems.application.cms.dto.query.ContenidoWebQuery;

public interface EditarContenidoWebUseCase {

    ContenidoWebQuery ejecutar(EditarContenidoCommand command);
}