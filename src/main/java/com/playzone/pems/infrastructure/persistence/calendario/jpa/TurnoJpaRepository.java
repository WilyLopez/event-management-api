package com.playzone.pems.infrastructure.persistence.calendario.jpa;

import com.playzone.pems.infrastructure.persistence.calendario.entity.TurnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurnoJpaRepository extends JpaRepository<TurnoEntity, Long> {

    Optional<TurnoEntity> findByCodigo(String codigo);
}