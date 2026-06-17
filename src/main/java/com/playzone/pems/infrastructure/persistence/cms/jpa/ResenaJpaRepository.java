package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.ResenaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResenaJpaRepository extends JpaRepository<ResenaEntity, Long> {

    @Query("SELECT r FROM ResenaEntity r WHERE r.aprobada = true ORDER BY r.createdAt DESC")
    Page<ResenaEntity> findAprobadas(Pageable pageable);

    @Query("SELECT r FROM ResenaEntity r WHERE r.aprobada = false AND r.aprobadaPor IS NULL ORDER BY r.createdAt ASC")
    Page<ResenaEntity> findPendientes(Pageable pageable);
}