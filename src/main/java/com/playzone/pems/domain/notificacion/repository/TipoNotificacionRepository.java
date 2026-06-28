package com.playzone.pems.domain.notificacion.repository;

import com.playzone.pems.domain.notificacion.model.TipoNotificacion;

import java.util.Optional;

public interface TipoNotificacionRepository {

    Optional<TipoNotificacion> findByCodigo(String codigo);
}
