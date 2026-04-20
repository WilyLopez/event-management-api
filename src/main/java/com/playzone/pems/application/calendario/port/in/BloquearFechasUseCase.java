package com.playzone.pems.application.calendario.port.in;

import com.playzone.pems.application.calendario.dto.command.BloquearFechasCommand;
import com.playzone.pems.domain.calendario.model.BloqueCalendario;

public interface BloquearFechasUseCase {

    BloqueCalendario ejecutar(BloquearFechasCommand command);

    void desactivar(Long idBloque);
}