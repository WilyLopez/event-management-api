package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.ConfiguracionPublicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionPublicaJpaRepository extends JpaRepository<ConfiguracionPublicaEntity, Long> {

    Optional<ConfiguracionPublicaEntity> findFirstByOrderByIdAsc();
}
