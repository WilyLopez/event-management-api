package com.playzone.pems.infrastructure.persistence.preferencia.jpa;

import com.playzone.pems.infrastructure.persistence.preferencia.entity.PreferenciaUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PreferenciaUsuarioJpaRepository extends JpaRepository<PreferenciaUsuarioEntity, UUID> {
}
