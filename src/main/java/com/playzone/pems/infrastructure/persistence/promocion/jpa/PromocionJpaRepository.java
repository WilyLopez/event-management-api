package com.playzone.pems.infrastructure.persistence.promocion.jpa;

import com.playzone.pems.infrastructure.persistence.promocion.entity.PromocionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PromocionJpaRepository extends JpaRepository<PromocionEntity, Long> {

    @Query("""
            SELECT p FROM PromocionEntity p
            WHERE p.esActivo = true
              AND p.esAutomatica = true
              AND p.fechaInicio <= :fecha
              AND (p.fechaFin IS NULL OR p.fechaFin >= :fecha)
              AND (p.sedeId IS NULL OR p.sedeId = :idSede)
              AND (p.tipoDiaCodigo IS NULL OR p.tipoDiaCodigo = :tipoDiaCodigo)
            """)
    List<PromocionEntity> findAutomaticasVigentes(
            @Param("idSede") Long idSede,
            @Param("tipoDiaCodigo") String tipoDiaCodigo,
            @Param("fecha") LocalDate fecha);
}
