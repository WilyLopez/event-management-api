package com.playzone.pems.application.notificacion.port.in;

import com.playzone.pems.application.notificacion.dto.query.NotificacionQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ObtenerNotificacionesUseCase {

    Page<NotificacionQuery> feedUsuario(UUID usuarioId, boolean soloNoLeidas, Pageable pageable);

    Page<NotificacionQuery> feedCliente(Long clienteId, boolean soloNoLeidas, Pageable pageable);

    long contarNoLeidasUsuario(UUID usuarioId);

    long contarNoLeidasCliente(Long clienteId);
}
