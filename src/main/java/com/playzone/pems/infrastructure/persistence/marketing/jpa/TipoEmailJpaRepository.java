package com.playzone.pems.infrastructure.persistence.marketing.jpa;

import com.playzone.pems.infrastructure.persistence.marketing.entity.TipoEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoEmailJpaRepository extends JpaRepository<TipoEmailEntity, String> {

    List<TipoEmailEntity> findByActivoTrue();
}
