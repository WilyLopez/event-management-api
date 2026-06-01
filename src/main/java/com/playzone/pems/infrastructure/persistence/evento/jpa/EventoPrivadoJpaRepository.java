package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EventoPrivadoJpaRepository extends JpaRepository<EventoPrivadoEntity, Long> {

    Page<EventoPrivadoEntity> findByCliente_Id(Long idCliente, Pageable pageable);

    Page<EventoPrivadoEntity> findBySede_IdAndEstado(Long idSede, EstadoEventoPrivado estado, Pageable pageable);

    Page<EventoPrivadoEntity> findBySede_IdAndFechaEventoBetween(
            Long idSede, LocalDate inicio, LocalDate fin, Pageable pageable);

    List<EventoPrivadoEntity> findBySede_IdAndFechaEventoBetween(Long idSede, LocalDate inicio, LocalDate fin);

    List<EventoPrivadoEntity> findBySede_IdAndFechaEvento(Long idSede, LocalDate fecha);

    @Query("""
            SELECT e FROM EventoPrivadoEntity e
            WHERE (CAST(:idSede AS long) IS NULL OR e.sede.id = :idSede)
              AND (:estadoEnum IS NULL OR e.estado = :estadoEnum)
              AND (CAST(:fecha AS localdate) IS NULL OR e.fechaEvento = :fecha)
              AND (:searchPattern IS NULL OR
                   LOWER(e.tipoEvento)             LIKE :searchPattern OR
                   LOWER(e.cliente.nombre)         LIKE :searchPattern OR
                   LOWER(e.cliente.correo)         LIKE :searchPattern
              )
            """)
    Page<EventoPrivadoEntity> buscarAdmin(
            @Param("idSede")    Long                idSede,
            @Param("estadoEnum")EstadoEventoPrivado estadoEnum,
            @Param("fecha")     LocalDate           fecha,
            @Param("searchPattern") String          searchPattern,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(e) > 0 FROM EventoPrivadoEntity e
            WHERE e.sede.id = :idSede AND e.fechaEvento = :fecha
              AND e.turno.id = :idTurno AND e.estado IN ('SOLICITADA','CONFIRMADA')
            """)
    boolean existsActivoBySedeAndFechaAndTurno(
            @Param("idSede") Long idSede,
            @Param("fecha")  LocalDate fecha,
            @Param("idTurno")Long idTurno);

    @Query("""
            SELECT COUNT(e) > 0 FROM EventoPrivadoEntity e
            WHERE e.sede.id = :idSede
              AND e.fechaEvento = :fecha
              AND e.estado IN ('SOLICITADA','CONFIRMADA')
            """)
    boolean existsActivoBySedeAndFecha(
            @Param("idSede") Long idSede,
            @Param("fecha")  LocalDate fecha);

    @Query("SELECT COALESCE(SUM(e.montoAdelanto), 0) FROM EventoPrivadoEntity e " +
           "WHERE e.sede.id = :idSede AND YEAR(e.fechaEvento) = :anio " +
           "AND MONTH(e.fechaEvento) = :mes AND e.estado <> 'CANCELADA'")
    BigDecimal sumAdelantosBySedeAndPeriodo(
            @Param("idSede") Long idSede,
            @Param("anio") int anio,
            @Param("mes") int mes);

    @Query("SELECT COALESCE(SUM(e.precioTotalContrato - e.montoAdelanto), 0) FROM EventoPrivadoEntity e " +
           "WHERE e.sede.id = :idSede AND YEAR(e.fechaEvento) = :anio AND MONTH(e.fechaEvento) = :mes " +
           "AND e.estado = 'CONFIRMADA' AND e.precioTotalContrato > e.montoAdelanto")
    BigDecimal sumSaldoPendienteBySedeAndMes(
            @Param("idSede") Long idSede,
            @Param("anio") int anio,
            @Param("mes") int mes);

    @Query("SELECT COUNT(e) FROM EventoPrivadoEntity e WHERE e.sede.id = :idSede AND e.estado = :estado")
    int countBySedeAndEstado(@Param("idSede") Long idSede, @Param("estado") EstadoEventoPrivado estado);

    @Query("""
            SELECT COUNT(e) FROM EventoPrivadoEntity e
            WHERE e.sede.id = :idSede
              AND e.fechaEvento BETWEEN :inicio AND :fin
              AND e.estado = :estado
            """)
    int countBySedeAndRangoAndEstado(
            @Param("idSede") Long idSede,
            @Param("inicio") LocalDate inicio,
            @Param("fin")    LocalDate fin,
            @Param("estado") EstadoEventoPrivado estado);

    @Query("""
            SELECT COUNT(e) FROM EventoPrivadoEntity e
            WHERE e.sede.id = :idSede
              AND e.estado = 'CONFIRMADA'
              AND e.precioTotalContrato IS NOT NULL
              AND e.montoAdelanto < e.precioTotalContrato
            """)
    int countConfirmadosConSaldo(@Param("idSede") Long idSede);
}