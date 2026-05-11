package com.playzone.pems.domain.auditoria.repository;

import com.playzone.pems.domain.auditoria.model.LogAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LogAuditoriaRepository {

    LogAuditoria save(LogAuditoria log);

    Optional<LogAuditoria> findById(Long id);

    Page<LogAuditoria> findByUsuario(Long idUsuarioAdmin, Pageable pageable);

    Page<LogAuditoria> findByModuloAndEntidad(
            String modulo, String entidad, Pageable pageable);

    Page<LogAuditoria> findByFechasBetween(
            LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    Page<LogAuditoria> findByFiltros(
            LocalDateTime desde, LocalDateTime hasta,
            Long idUsuario, String modulo, String accion, String entidad,
            Pageable pageable);
}
