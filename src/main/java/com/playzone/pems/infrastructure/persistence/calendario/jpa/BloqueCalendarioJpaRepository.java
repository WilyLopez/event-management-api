package com.playzone.pems.infrastructure.persistence.calendario.jpa;

import com.playzone.pems.infrastructure.persistence.calendario.entity.BloqueCalendarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BloqueCalendarioJpaRepository extends JpaRepository<BloqueCalendarioEntity, Long> {

    List<BloqueCalendarioEntity> findBySede_IdAndActivoTrue(Long idSede);

    @Query("SELECT COUNT(b) > 0 FROM BloqueCalendarioEntity b WHERE b.sede.id = :idSede AND b.activo = true AND :fecha BETWEEN b.fechaInicio AND b.fechaFin")
    boolean existsBloqueActivoEnFecha(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(b) > 0 FROM BloqueCalendarioEntity b WHERE b.sede.id = :idSede AND b.activo = true AND b.fechaFin >= :inicio AND b.fechaInicio <= :fin")
    boolean existsSolapamientoEnRango(@Param("idSede") Long idSede, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}