package com.playzone.pems.infrastructure.persistence.notificacion.jpa;

import com.playzone.pems.infrastructure.persistence.notificacion.entity.NotificacionEntregaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionEntregaJpaRepository extends JpaRepository<NotificacionEntregaEntity, Long> {
}
