package com.playzone.pems.infrastructure.persistence.calendario.jpa;

import com.playzone.pems.infrastructure.persistence.calendario.entity.ConfiguracionCalendarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionCalendarioJpaRepository
        extends JpaRepository<ConfiguracionCalendarioEntity, Long> {

    Optional<ConfiguracionCalendarioEntity> findBySede_Id(Long idSede);
}
