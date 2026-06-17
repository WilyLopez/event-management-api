package com.playzone.pems.application.preferencia.port.in;

import com.playzone.pems.application.preferencia.dto.response.PreferenciaAdminResponse;

import java.util.UUID;

public interface ObtenerPreferenciaAdminUseCase {

    PreferenciaAdminResponse obtener(UUID usuarioId);
}
