package com.playzone.pems.application.notificacion.port.in;

import com.playzone.pems.application.notificacion.dto.command.CrearNotificacionCommand;
import com.playzone.pems.application.notificacion.dto.query.NotificacionQuery;

public interface CrearNotificacionUseCase {

    NotificacionQuery ejecutar(CrearNotificacionCommand cmd);
}
