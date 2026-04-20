package com.playzone.pems.domain.auditoria.repository;

import com.playzone.pems.domain.auditoria.model.LogAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface LogAuditoriaRepository {

    LogAuditoria save(LogAuditoria log);

    Page<LogAuditoria> findByUsuario(Long idUsuarioAdmin, Pageable pageable);

    Page<LogAuditoria> findByModuloAndEntidad(
            String modulo, String entidad, Pageable pageable);

    Page<LogAuditoria> findByFechasBetween(
            LocalDateTime desde, LocalDateTime hasta, Pageable pageable);
}