package com.playzone.pems.infrastructure.persistence.usuario.jpa;

import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeJpaRepository extends JpaRepository<SedeEntity, Long> {

    List<SedeEntity> findByActivoTrue();

    boolean existsByRuc(String ruc);
}