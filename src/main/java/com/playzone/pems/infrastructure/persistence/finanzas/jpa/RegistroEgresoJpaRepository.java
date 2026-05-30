package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
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

    @Query(value = "SELECT r FROM RegistroEgresoEntity r JOIN FETCH r.tipoEgreso WHERE r.sede.id = :idSede",
           countQuery = "SELECT COUNT(r) FROM RegistroEgresoEntity r WHERE r.sede.id = :idSede")
    Page<RegistroEgresoEntity> findBySede_IdWithTipo(@Param("idSede") Long idSede, Pageable pageable);

    @Query("SELECT r FROM RegistroEgresoEntity r JOIN FETCH r.tipoEgreso " +
           "WHERE r.sede.id = :idSede AND r.periodoAnio = :anio AND r.periodoMes = :mes")
    List<RegistroEgresoEntity> findBySede_IdAndPeriodoWithTipo(
            @Param("idSede") Long idSede, @Param("anio") int anio, @Param("mes") int mes);

    @Query("SELECT r FROM RegistroEgresoEntity r JOIN FETCH r.tipoEgreso " +
           "WHERE r.sede.id = :idSede AND r.fecha BETWEEN :inicio AND :fin")
    List<RegistroEgresoEntity> findBySede_IdAndFechaBetweenWithTipo(
            @Param("idSede") Long idSede, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(r.monto), 0) FROM RegistroEgresoEntity r " +
           "WHERE r.sede.id = :idSede AND r.periodoAnio = :anio AND r.periodoMes = :mes")
    BigDecimal sumMontoBySedeAndPeriodo(
            @Param("idSede") Long idSede,
            @Param("anio") int anio,
            @Param("mes") int mes);

    @Query("SELECT r.tipoEgreso.id, COALESCE(SUM(r.monto), 0) FROM RegistroEgresoEntity r " +
           "WHERE r.sede.id = :idSede AND r.periodoAnio = :anio AND r.periodoMes = :mes " +
           "GROUP BY r.tipoEgreso.id")
    List<Object[]> sumMontoAgrupadoPorTipo(
            @Param("idSede") Long idSede, @Param("anio") int anio, @Param("mes") int mes);

    @Query("SELECT COALESCE(SUM(r.monto), 0) FROM RegistroEgresoEntity r " +
           "WHERE r.sede.id = :idSede AND r.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumMontoBySedeAndRango(
            @Param("idSede") Long idSede, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(r.monto), 0) FROM RegistroEgresoEntity r " +
           "WHERE r.sede.id = :idSede AND r.periodoAnio = :anio AND r.periodoMes = :mes " +
           "AND r.tipoEgreso.categoria = :categoria")
    BigDecimal sumMontoBySedeAndPeriodoAndCategoria(
            @Param("idSede") Long idSede, @Param("anio") int anio, @Param("mes") int mes,
            @Param("categoria") CategoriaEgreso categoria);
}
