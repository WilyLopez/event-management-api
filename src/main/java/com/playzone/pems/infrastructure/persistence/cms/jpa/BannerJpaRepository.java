package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.BannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BannerJpaRepository extends JpaRepository<BannerEntity, Long> {

    @Query("""
            SELECT b FROM BannerEntity b
            WHERE b.activo = true
              AND b.fechaInicio <= :fecha
              AND (b.fechaFin IS NULL OR b.fechaFin >= :fecha)
              AND (b.sede IS NULL OR b.sede.id = :idSede)
            ORDER BY b.orden ASC
            """)
    List<BannerEntity> findVisiblesBySedeAndFecha(
            @Param("idSede") Long idSede,
            @Param("fecha") LocalDate fecha);
}