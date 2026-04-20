package com.playzone.pems.infrastructure.persistence.promocion.jpa;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.infrastructure.persistence.promocion.entity.PromocionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PromocionJpaRepository extends JpaRepository<PromocionEntity, Long> {

    @Query("""
            SELECT p FROM PromocionEntity p
            WHERE p.activo = true
              AND p.esAutomatica = true
              AND p.fechaInicio <= :fecha
              AND (p.fechaFin IS NULL OR p.fechaFin >= :fecha)
              AND (p.sede IS NULL OR p.sede.id = :idSede)
              AND (p.soloTipoDia IS NULL OR p.soloTipoDia = :tipoDia)
            """)
    List<PromocionEntity> findAutomaticasVigentes(
            @Param("idSede") Long idSede,
            @Param("tipoDia") TipoDia tipoDia,
            @Param("fecha") LocalDate fecha);
}