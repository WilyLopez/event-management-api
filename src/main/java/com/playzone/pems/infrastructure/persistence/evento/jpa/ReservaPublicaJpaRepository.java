package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaPublicaJpaRepository extends JpaRepository<ReservaPublicaEntity, Long> {

    Optional<ReservaPublicaEntity> findByNumeroTicket(String numeroTicket);

    Page<ReservaPublicaEntity> findByCliente_Id(Long idCliente, Pageable pageable);

    Page<ReservaPublicaEntity> findBySede_IdAndFechaEvento(Long idSede, LocalDate fecha, Pageable pageable);

    List<ReservaPublicaEntity> findBySede_IdAndFechaEvento(Long idSede, LocalDate fecha);

    List<ReservaPublicaEntity> findBySede_IdAndFechaEventoBetween(Long idSede, LocalDate inicio, LocalDate fin);

    Page<ReservaPublicaEntity> findBySede_IdAndEstado(Long idSede, EstadoReservaPublica estado, Pageable pageable);

    List<ReservaPublicaEntity> findBySede_IdAndFechaEventoAndEstado(
            Long idSede, LocalDate fecha, EstadoReservaPublica estado);

    @Query("""
            SELECT r FROM ReservaPublicaEntity r
            WHERE (CAST(:idSede AS long) IS NULL OR r.sede.id = :idSede)
              AND (:estadoEnum IS NULL OR r.estado = :estadoEnum)
              AND (CAST(:fecha AS localdate) IS NULL OR r.fechaEvento = :fecha)
              AND (CAST(:ingresado AS boolean) IS NULL OR r.ingresado = :ingresado)
              AND (CAST(:esReprogramacion AS boolean) IS NULL OR r.esReprogramacion = :esReprogramacion)
              AND (:searchPattern IS NULL OR
                   LOWER(r.numeroTicket)      LIKE :searchPattern OR
                   LOWER(r.nombreNino)        LIKE :searchPattern OR
                   LOWER(r.nombreAcompanante) LIKE :searchPattern
              )
            """)
    Page<ReservaPublicaEntity> buscarAdmin(
            @Param("idSede")           Long                 idSede,
            @Param("estadoEnum")       EstadoReservaPublica estadoEnum,
            @Param("fecha")            LocalDate            fecha,
            @Param("ingresado")        Boolean              ingresado,
            @Param("esReprogramacion") Boolean              esReprogramacion,
            @Param("searchPattern")    String               searchPattern,
            Pageable pageable
    );

    @Query("SELECT COUNT(r) FROM ReservaPublicaEntity r WHERE r.sede.id = :idSede AND r.fechaEvento = :fecha AND r.estado IN ('CONFIRMADA', 'COMPLETADA')")
    int countConfirmadasBySedeAndFecha(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(r) FROM ReservaPublicaEntity r WHERE r.sede.id = :idSede AND r.fechaEvento = :fecha AND r.estado = :estado")
    int countBySedeAndFechaAndEstado(
            @Param("idSede") Long idSede,
            @Param("fecha") LocalDate fecha,
            @Param("estado") EstadoReservaPublica estado);

    @Query("SELECT COUNT(r) FROM ReservaPublicaEntity r WHERE r.sede.id = :idSede AND r.fechaEvento = :fecha AND r.ingresado = TRUE")
    int countIngresadosBySedeAndFecha(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);

    @Query("SELECT COALESCE(SUM(r.totalPagado), 0) FROM ReservaPublicaEntity r WHERE r.sede.id = :idSede AND r.fechaEvento = :fecha AND r.estado NOT IN ('CANCELADA')")
    BigDecimal sumIngresosBySedeAndFecha(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);

    boolean existsByNumeroTicket(String numeroTicket);
}