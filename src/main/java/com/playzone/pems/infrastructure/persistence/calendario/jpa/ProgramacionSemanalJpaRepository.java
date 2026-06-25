package com.playzone.pems.infrastructure.persistence.calendario.jpa;

import com.playzone.pems.infrastructure.persistence.calendario.entity.ProgramacionSemanalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProgramacionSemanalJpaRepository
        extends JpaRepository<ProgramacionSemanalEntity, Long> {

    @Query("""
        SELECT p FROM ProgramacionSemanalEntity p
        WHERE p.sede.id = :idSede
          AND p.estado  = 'ACTIVA'
          AND p.semanaInicio <= :fin
          AND p.semanaFin    >= :inicio
        ORDER BY p.semanaInicio
        """)
    List<ProgramacionSemanalEntity> findActivasBySedeAndRango(
            @Param("idSede") Long idSede,
            @Param("inicio") LocalDate inicio,
            @Param("fin")    LocalDate fin);

    @Query("""
        SELECT p FROM ProgramacionSemanalEntity p
        WHERE p.sede.id    = :idSede
          AND p.estado     = 'ACTIVA'
          AND p.semanaFin >= :hoy
        ORDER BY p.semanaInicio
        """)
    List<ProgramacionSemanalEntity> findActivasFuturasBySede(
            @Param("idSede") Long idSede,
            @Param("hoy")    LocalDate hoy);

    @Query("""
        SELECT COUNT(p) > 0 FROM ProgramacionSemanalEntity p
        WHERE p.sede.id    = :idSede
          AND p.estado     = 'ACTIVA'
          AND p.semanaInicio <= :fecha
          AND p.semanaFin    >= :fecha
        """)
    boolean existeActivaEnFecha(
            @Param("idSede") Long idSede,
            @Param("fecha")  LocalDate fecha);

    @Query("""
        SELECT COUNT(p) > 0 FROM ProgramacionSemanalEntity p
        WHERE p.sede.id    = :idSede
          AND p.estado     = 'ACTIVA'
          AND p.semanaInicio <= :fin
          AND p.semanaFin    >= :inicio
        """)
    boolean existeSolapamiento(
            @Param("idSede") Long idSede,
            @Param("inicio") LocalDate inicio,
            @Param("fin")    LocalDate fin);


    @Query("""
        SELECT s.id FROM SedeEntity s
        WHERE NOT EXISTS (
            SELECT p FROM ProgramacionSemanalEntity p
            WHERE p.sede.id      = s.id
              AND p.estado       = 'ACTIVA'
              AND p.semanaInicio <= :semanaFin
              AND p.semanaFin    >= :semanaInicio
        )
        """)
    List<Long> findSedeIdsSinProgramacionEnSemana(
            @Param("semanaInicio") LocalDate semanaInicio,
            @Param("semanaFin")    LocalDate semanaFin);

    @Modifying
    @Query("""
        UPDATE ProgramacionSemanalEntity p
        SET p.estado = 'CANCELADA'
        WHERE p.id = :id
        """)
    void cancelar(@Param("id") Long id);
}
