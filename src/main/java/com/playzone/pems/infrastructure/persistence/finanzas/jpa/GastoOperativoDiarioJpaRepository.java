package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.infrastructure.persistence.finanzas.entity.GastoOperativoDiarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GastoOperativoDiarioJpaRepository extends JpaRepository<GastoOperativoDiarioEntity, Long> {

    List<GastoOperativoDiarioEntity> findBySede_IdAndFecha(Long idSede, LocalDate fecha);

    List<GastoOperativoDiarioEntity> findBySede_IdAndFechaBetween(Long idSede, LocalDate inicio, LocalDate fin);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM GastoOperativoDiarioEntity g WHERE g.sede.id = :idSede AND g.fecha = :fecha")
    BigDecimal sumMontoBySedeAndFecha(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM GastoOperativoDiarioEntity g " +
           "WHERE g.sede.id = :idSede AND YEAR(g.fecha) = :anio AND MONTH(g.fecha) = :mes")
    BigDecimal sumMontoBySedeAndPeriodo(
            @Param("idSede") Long idSede,
            @Param("anio") int anio,
            @Param("mes") int mes);
}
