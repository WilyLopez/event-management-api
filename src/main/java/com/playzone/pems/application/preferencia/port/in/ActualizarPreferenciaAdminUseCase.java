package com.playzone.pems.application.preferencia.port.in;

import com.playzone.pems.application.preferencia.dto.command.ActualizarPreferenciaAdminCommand;
import com.playzone.pems.application.preferencia.dto.response.PreferenciaAdminResponse;

import java.util.Map;
import java.util.UUID;

public interface ActualizarPreferenciaAdminUseCase {

    PreferenciaAdminResponse actualizar(UUID usuarioId, ActualizarPreferenciaAdminCommand command);

    PreferenciaAdminResponse parchear(UUID usuarioId, Map<String, Object> patch);

    PreferenciaAdminResponse resetear(UUID usuarioId);
}
