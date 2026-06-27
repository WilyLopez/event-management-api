package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.AperturaCajaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AperturaCajaJpaRepository extends JpaRepository<AperturaCajaEntity, Long> {

    Optional<AperturaCajaEntity> findBySede_IdAndFecha(Long idSede, LocalDate fecha);

    Optional<AperturaCajaEntity> findBySede_IdAndEstado(Long idSede, EstadoCaja estado);

    Optional<AperturaCajaEntity> findBySede_IdAndEstadoAndFecha(Long idSede, EstadoCaja estado, LocalDate fecha);

    List<AperturaCajaEntity> findBySede_IdAndFechaBetweenOrderByFechaAsc(
            Long idSede, LocalDate inicio, LocalDate fin);

    @Modifying
    @Query("UPDATE AperturaCajaEntity a SET a.totalIngresos = a.totalIngresos + :delta WHERE a.id = :id")
    int incrementarIngresos(@Param("id") Long id, @Param("delta") BigDecimal delta);

    @Modifying
    @Query("UPDATE AperturaCajaEntity a SET a.totalEgresos = a.totalEgresos + :delta WHERE a.id = :id")
    int incrementarEgresos(@Param("id") Long id, @Param("delta") BigDecimal delta);
}
