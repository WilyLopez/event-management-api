package com.playzone.pems.infrastructure.persistence.auditoria.adapter;

import com.playzone.pems.domain.auditoria.model.LogAuditoria;
import com.playzone.pems.domain.auditoria.repository.LogAuditoriaRepository;
import com.playzone.pems.domain.usuario.repository.PerfilUsuarioRepository;
import com.playzone.pems.infrastructure.persistence.auditoria.entity.LogAuditoriaEntity;
import com.playzone.pems.infrastructure.persistence.auditoria.jpa.LogAuditoriaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditoriaPersistenceAdapter implements LogAuditoriaRepository {

    private final LogAuditoriaJpaRepository logJpa;
    private final PerfilUsuarioRepository   perfilUsuarioRepository;

    @Override
    @Transactional
    public LogAuditoria save(LogAuditoria log) {
        LogAuditoriaEntity entity = LogAuditoriaEntity.builder()
                .usuarioId(log.getIdUsuarioAdmin())
                .accion(log.getAccion())
                .modulo(log.getModulo())
                .entidadAfectada(log.getEntidadAfectada())
                .idEntidad(log.getIdEntidad())
                .valorAnterior(log.getValorAnterior())
                .valorNuevo(log.getValorNuevo())
                .descripcion(log.getDescripcion())
                .ipOrigen(log.getIpOrigen())
                .userAgent(log.getUserAgent())
                .nivel(log.getNivel() != null ? log.getNivel() : "INFO")
                .resultado(log.getResultado() != null ? log.getResultado() : "EXITOSO")
                .build();

        return toDomain(logJpa.save(entity));
    }

    @Override
    public Optional<LogAuditoria> findById(Long id) {
        return logJpa.findById(id).map(this::toDomain);
    }

    @Override
    public Page<LogAuditoria> findByUsuario(UUID idUsuarioAdmin, Pageable pageable) {
        return logJpa.findByUsuarioIdOrderByFechaLogDesc(idUsuarioAdmin, pageable)
                .map(this::toDomain);
    }

    @Override
    public Page<LogAuditoria> findByModuloAndEntidad(String modulo, String entidad, Pageable pageable) {
        return logJpa.findByModuloAndEntidadAfectadaOrderByFechaLogDesc(modulo, entidad, pageable)
                .map(this::toDomain);
    }

    @Override
    public Page<LogAuditoria> findByFechasBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        return logJpa.findByFechaLogBetweenOrderByFechaLogDesc(desde, hasta, pageable)
                .map(this::toDomain);
    }

    @Override
    public Page<LogAuditoria> findByFiltros(LocalDateTime desde, LocalDateTime hasta,
                                             UUID idUsuario, String modulo, String accion,
                                             String entidad, Pageable pageable) {
        return logJpa.findByFiltros(desde, hasta, idUsuario, modulo, accion, entidad, pageable)
                .map(this::toDomain);
    }

    private LogAuditoria toDomain(LogAuditoriaEntity e) {
        return LogAuditoria.builder()
                .id(e.getId())
                .idUsuarioAdmin(e.getUsuarioId())
                .nombreUsuario(e.getUsuarioId() != null
                        ? perfilUsuarioRepository.buscarPorId(e.getUsuarioId())
                                .map(u -> u.getNombreCompleto()).orElse(null)
                        : null)
                .accion(e.getAccion())
                .modulo(e.getModulo())
                .entidadAfectada(e.getEntidadAfectada())
                .idEntidad(e.getIdEntidad())
                .valorAnterior(e.getValorAnterior())
                .valorNuevo(e.getValorNuevo())
                .descripcion(e.getDescripcion())
                .ipOrigen(e.getIpOrigen())
                .userAgent(e.getUserAgent())
                .nivel(e.getNivel())
                .resultado(e.getResultado())
                .fechaLog(e.getFechaLog())
                .build();
    }
}
