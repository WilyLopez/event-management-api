package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.NovedadLocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NovedadLocalJpaRepository extends JpaRepository<NovedadLocalEntity, Long> {
    List<NovedadLocalEntity> findByActivaTrueOrderByPrioridadAsc();
    List<NovedadLocalEntity> findByVisibleHomeTrueAndActivaTrueOrderByPrioridadAsc();
}
