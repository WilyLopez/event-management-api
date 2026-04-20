package com.playzone.pems.infrastructure.persistence.auditoria.jpa;

import com.playzone.pems.infrastructure.persistence.auditoria.entity.LogAuditoriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface LogAuditoriaJpaRepository extends JpaRepository<LogAuditoriaEntity, Long> {

    Page<LogAuditoriaEntity> findByUsuarioAdmin_IdOrderByTimestampDesc(Long idUsuarioAdmin, Pageable pageable);

    Page<LogAuditoriaEntity> findByModuloAndEntidadAfectadaOrderByTimestampDesc(
            String modulo, String entidad, Pageable pageable);

    Page<LogAuditoriaEntity> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime desde, LocalDateTime hasta, Pageable pageable);
}