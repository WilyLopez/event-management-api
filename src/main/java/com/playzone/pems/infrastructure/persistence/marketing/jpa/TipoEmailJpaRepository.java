package com.playzone.pems.infrastructure.persistence.marketing.jpa;

import com.playzone.pems.infrastructure.persistence.marketing.entity.TipoEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoEmailJpaRepository extends JpaRepository<TipoEmailEntity, Long> {

    List<TipoEmailEntity> findByActivoTrue();

    Optional<TipoEmailEntity> findByCodigo(String codigo);
}
