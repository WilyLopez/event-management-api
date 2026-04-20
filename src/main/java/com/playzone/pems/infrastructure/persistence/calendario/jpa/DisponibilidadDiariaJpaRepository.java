package com.playzone.pems.infrastructure.persistence.calendario.jpa;

import com.playzone.pems.infrastructure.persistence.calendario.entity.DisponibilidadDiariaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadDiariaJpaRepository extends JpaRepository<DisponibilidadDiariaEntity, Long> {

    Optional<DisponibilidadDiariaEntity> findBySede_IdAndFecha(Long idSede, LocalDate fecha);

    List<DisponibilidadDiariaEntity> findBySede_IdAndFechaBetween(Long idSede, LocalDate inicio, LocalDate fin);

    @Modifying
    @Query("UPDATE DisponibilidadDiariaEntity d SET d.aforoPublicoActual = d.aforoPublicoActual + 1 WHERE d.sede.id = :idSede AND d.fecha = :fecha")
    void incrementarAforo(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);

    @Modifying
    @Query("UPDATE DisponibilidadDiariaEntity d SET d.aforoPublicoActual = GREATEST(0, d.aforoPublicoActual - 1) WHERE d.sede.id = :idSede AND d.fecha = :fecha")
    void decrementarAforo(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);

    @Modifying
    @Query("UPDATE DisponibilidadDiariaEntity d SET d.accesoPublicoActivo = false WHERE d.sede.id = :idSede AND d.fecha = :fecha")
    void bloquearAccesoPublico(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);
}