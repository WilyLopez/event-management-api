package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.ResenaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResenaJpaRepository extends JpaRepository<ResenaEntity, Long> {

    Page<ResenaEntity> findByAprobadaTrueOrderByFechaCreacionDesc(Pageable pageable);

    @Query("SELECT r FROM ResenaEntity r WHERE r.aprobada = false AND r.usuarioAprueba IS NULL ORDER BY r.fechaCreacion ASC")
    Page<ResenaEntity> findPendientes(Pageable pageable);
}