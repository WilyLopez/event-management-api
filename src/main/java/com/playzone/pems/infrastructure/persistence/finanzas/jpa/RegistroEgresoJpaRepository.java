package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.infrastructure.persistence.finanzas.entity.RegistroEgresoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RegistroEgresoJpaRepository extends JpaRepository<RegistroEgresoEntity, Long> {

    Page<RegistroEgresoEntity> findBySede_Id(Long idSede, Pageable pageable);

    List<RegistroEgresoEntity> findBySede_IdAndPeriodoAnioAndPeriodoMes(
            Long idSede, Integer periodoAnio, Integer periodoMes);

    List<RegistroEgresoEntity> findBySede_IdAndFechaBetween(
            Long idSede, LocalDate inicio, LocalDate fin);

    @Query("SELECT COALESCE(SUM(r.monto), 0) FROM RegistroEgresoEntity r " +
           "WHERE r.sede.id = :idSede AND r.periodoAnio = :anio AND r.periodoMes = :mes")
    BigDecimal sumMontoBySedeAndPeriodo(
            @Param("idSede") Long idSede,
            @Param("anio") int anio,
            @Param("mes") int mes);
}
