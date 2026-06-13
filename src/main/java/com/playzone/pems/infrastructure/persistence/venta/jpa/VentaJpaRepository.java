package com.playzone.pems.infrastructure.persistence.venta.jpa;

import com.playzone.pems.infrastructure.persistence.venta.entity.VentaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface VentaJpaRepository extends JpaRepository<VentaEntity, Long> {

    Page<VentaEntity> findBySede_IdAndCreatedAtBetween(
            Long idSede, OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable);

    Page<VentaEntity> findByCreatedBy(UUID createdBy, Pageable pageable);

    List<VentaEntity> findByEventoId(Long eventoId);
}
