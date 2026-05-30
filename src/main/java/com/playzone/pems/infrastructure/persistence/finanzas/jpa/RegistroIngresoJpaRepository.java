package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.RegistroIngresoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RegistroIngresoJpaRepository extends JpaRepository<RegistroIngresoEntity, Long> {

    @Query(value = "SELECT r FROM RegistroIngresoEntity r JOIN FETCH r.tipoIngreso WHERE r.sede.id = :idSede ORDER BY r.fecha DESC",
           countQuery = "SELECT COUNT(r) FROM RegistroIngresoEntity r WHERE r.sede.id = :idSede")
    Page<RegistroIngresoEntity> findBySede_IdWithTipo(@Param("idSede") Long idSede, Pageable pageable);

    @Query("SELECT r FROM RegistroIngresoEntity r JOIN FETCH r.tipoIngreso " +
           "WHERE r.sede.id = :idSede AND r.fecha BETWEEN :inicio AND :fin ORDER BY r.fecha DESC")
    List<RegistroIngresoEntity> findBySede_IdAndFechaBetweenWithTipo(
            @Param("idSede") Long idSede, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT r FROM RegistroIngresoEntity r JOIN FETCH r.tipoIngreso " +
           "WHERE r.sede.id = :idSede AND YEAR(r.fecha) = :anio AND MONTH(r.fecha) = :mes ORDER BY r.fecha DESC")
    List<RegistroIngresoEntity> findBySede_IdAndPeriodoWithTipo(
            @Param("idSede") Long idSede, @Param("anio") int anio, @Param("mes") int mes);

    @Query("SELECT COALESCE(SUM(r.monto), 0) FROM RegistroIngresoEntity r " +
           "WHERE r.sede.id = :idSede AND YEAR(r.fecha) = :anio AND MONTH(r.fecha) = :mes")
    BigDecimal sumMontoBySedeAndPeriodo(
            @Param("idSede") Long idSede, @Param("anio") int anio, @Param("mes") int mes);

    @Query("SELECT COALESCE(SUM(r.monto), 0) FROM RegistroIngresoEntity r " +
           "WHERE r.sede.id = :idSede AND r.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumMontoBySedeAndRango(
            @Param("idSede") Long idSede, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(r.monto), 0) FROM RegistroIngresoEntity r " +
           "WHERE r.sede.id = :idSede AND YEAR(r.fecha) = :anio AND MONTH(r.fecha) = :mes " +
           "AND r.tipoIngreso.categoria = :categoria")
    BigDecimal sumMontoBySedeAndPeriodoAndCategoria(
            @Param("idSede") Long idSede, @Param("anio") int anio, @Param("mes") int mes,
            @Param("categoria") CategoriaIngreso categoria);

    @Query("SELECT r.tipoIngreso.id, COALESCE(SUM(r.monto), 0) FROM RegistroIngresoEntity r " +
           "WHERE r.sede.id = :idSede AND YEAR(r.fecha) = :anio AND MONTH(r.fecha) = :mes " +
           "GROUP BY r.tipoIngreso.id")
    List<Object[]> sumMontoAgrupadoPorTipo(
            @Param("idSede") Long idSede, @Param("anio") int anio, @Param("mes") int mes);
}
