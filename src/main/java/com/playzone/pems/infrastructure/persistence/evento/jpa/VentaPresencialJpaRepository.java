package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.infrastructure.persistence.evento.entity.VentaPresencialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaPresencialJpaRepository extends JpaRepository<VentaPresencialEntity, Long> {
}
