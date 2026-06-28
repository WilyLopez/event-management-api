package com.playzone.pems.infrastructure.persistence.notificacion.jpa;

import com.playzone.pems.infrastructure.persistence.notificacion.entity.TipoNotificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoNotificacionJpaRepository extends JpaRepository<TipoNotificacionEntity, String> {
}
