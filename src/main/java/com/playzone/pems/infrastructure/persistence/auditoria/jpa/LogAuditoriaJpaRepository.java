package com.playzone.pems.infrastructure.persistence.auditoria.jpa;

import com.playzone.pems.infrastructure.persistence.auditoria.entity.LogAuditoriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LogAuditoriaJpaRepository extends JpaRepository<LogAuditoriaEntity, Long> {

    Page<LogAuditoriaEntity> findByUsuarioAdmin_IdOrderByFechaLogDesc(Long idUsuarioAdmin, Pageable pageable);

    Page<LogAuditoriaEntity> findByModuloAndEntidadAfectadaOrderByFechaLogDesc(
            String modulo, String entidad, Pageable pageable);

    Page<LogAuditoriaEntity> findByFechaLogBetweenOrderByFechaLogDesc(
            LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    @Query(value = """
            SELECT l FROM LogAuditoriaEntity l
            LEFT JOIN FETCH l.usuarioAdmin u
            WHERE l.fechaLog BETWEEN :desde AND :hasta
              AND (:idUsuario IS NULL OR u.id = :idUsuario)
              AND (:modulo   IS NULL OR LOWER(l.modulo) LIKE LOWER(CONCAT('%', :modulo, '%')))
              AND (:accion   IS NULL OR l.accion = :accion)
              AND (:entidad  IS NULL OR LOWER(l.entidadAfectada) LIKE LOWER(CONCAT('%', :entidad, '%')))
            ORDER BY l.fechaLog DESC
            """,
           countQuery = """
            SELECT COUNT(l) FROM LogAuditoriaEntity l
            LEFT JOIN l.usuarioAdmin u
            WHERE l.fechaLog BETWEEN :desde AND :hasta
              AND (:idUsuario IS NULL OR u.id = :idUsuario)
              AND (:modulo   IS NULL OR LOWER(l.modulo) LIKE LOWER(CONCAT('%', :modulo, '%')))
              AND (:accion   IS NULL OR l.accion = :accion)
              AND (:entidad  IS NULL OR LOWER(l.entidadAfectada) LIKE LOWER(CONCAT('%', :entidad, '%')))
            """)
    Page<LogAuditoriaEntity> findByFiltros(
            @Param("desde")     LocalDateTime desde,
            @Param("hasta")     LocalDateTime hasta,
            @Param("idUsuario") Long idUsuario,
            @Param("modulo")    String modulo,
            @Param("accion")    String accion,
            @Param("entidad")   String entidad,
            Pageable pageable);
}
