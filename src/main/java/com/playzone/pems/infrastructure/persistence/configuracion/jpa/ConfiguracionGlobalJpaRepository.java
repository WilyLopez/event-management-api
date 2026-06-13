package com.playzone.pems.infrastructure.persistence.configuracion.jpa;

import com.playzone.pems.infrastructure.persistence.configuracion.entity.ConfiguracionGlobalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionGlobalJpaRepository
        extends JpaRepository<ConfiguracionGlobalEntity, String> {
}
