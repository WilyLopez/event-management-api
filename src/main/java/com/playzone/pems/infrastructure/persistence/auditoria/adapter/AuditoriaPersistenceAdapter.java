package com.playzone.pems.infrastructure.persistence.auditoria.adapter;

import com.playzone.pems.domain.auditoria.model.LogAuditoria;
import com.playzone.pems.domain.auditoria.repository.LogAuditoriaRepository;
import com.playzone.pems.infrastructure.persistence.auditoria.entity.LogAuditoriaEntity;
import com.playzone.pems.infrastructure.persistence.auditoria.jpa.LogAuditoriaJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditoriaPersistenceAdapter implements LogAuditoriaRepository {

    private final LogAuditoriaJpaRepository  logJpa;
    private final UsuarioAdminJpaRepository  adminJpa;

    @Override
    @Transactional
    public LogAuditoria save(LogAuditoria log) {
        var usuario = log.getIdUsuarioAdmin() != null
                ? adminJpa.findById(log.getIdUsuarioAdmin()).orElse(null) : null;

        LogAuditoriaEntity entity = LogAuditoriaEntity.builder()
                .usuarioAdmin(usuario)
                .accion(log.getAccion())
                .modulo(log.getModulo())
                .entidadAfectada(log.getEntidadAfectada())
                .idEntidad(log.getIdEntidad())
                .valorAnterior(log.getValorAnterior())
                .valorNuevo(log.getValorNuevo())
                .descripcion(log.getDescripcion())
                .ipOrigen(log.getIpOrigen())
                .userAgent(log.getUserAgent())
                .build();

        LogAuditoriaEntity saved = logJpa.save(entity);

        return LogAuditoria.builder()
                .id(saved.getId())
                .idUsuarioAdmin(saved.getUsuarioAdmin() != null ? saved.getUsuarioAdmin().getId() : null)
                .accion(saved.getAccion())
                .modulo(saved.getModulo())
                .entidadAfectada(saved.getEntidadAfectada())
                .idEntidad(saved.getIdEntidad())
                .valorAnterior(saved.getValorAnterior())
                .valorNuevo(saved.getValorNuevo())
                .descripcion(saved.getDescripcion())
                .ipOrigen(saved.getIpOrigen())
                .userAgent(saved.getUserAgent())
                .timestamp(saved.getTimestamp())
                .build();
    }

    @Override
    public Page<LogAuditoria> findByUsuario(Long idUsuarioAdmin, Pageable pageable) {
        return logJpa.findByUsuarioAdmin_IdOrderByTimestampDesc(idUsuarioAdmin, pageable)
                .map(this::toDomain);
    }

    @Override
    public Page<LogAuditoria> findByModuloAndEntidad(String modulo, String entidad, Pageable pageable) {
        return logJpa.findByModuloAndEntidadAfectadaOrderByTimestampDesc(modulo, entidad, pageable)
                .map(this::toDomain);
    }

    @Override
    public Page<LogAuditoria> findByFechasBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        return logJpa.findByTimestampBetweenOrderByTimestampDesc(desde, hasta, pageable)
                .map(this::toDomain);
    }

    private LogAuditoria toDomain(LogAuditoriaEntity e) {
        return LogAuditoria.builder()
                .id(e.getId())
                .idUsuarioAdmin(e.getUsuarioAdmin() != null ? e.getUsuarioAdmin().getId() : null)
                .accion(e.getAccion())
                .modulo(e.getModulo())
                .entidadAfectada(e.getEntidadAfectada())
                .idEntidad(e.getIdEntidad())
                .valorAnterior(e.getValorAnterior())
                .valorNuevo(e.getValorNuevo())
                .descripcion(e.getDescripcion())
                .ipOrigen(e.getIpOrigen())
                .userAgent(e.getUserAgent())
                .timestamp(e.getTimestamp())
                .build();
    }
}