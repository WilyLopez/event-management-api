package com.playzone.pems.infrastructure.persistence.configuracion.jpa;

import com.playzone.pems.infrastructure.persistence.configuracion.entity.ConfiguracionSistemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionSistemaJpaRepository
        extends JpaRepository<ConfiguracionSistemaEntity, Long> {

    Optional<ConfiguracionSistemaEntity> findByClave(String clave);
}
