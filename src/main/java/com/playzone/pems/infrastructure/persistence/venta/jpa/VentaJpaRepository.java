package com.playzone.pems.infrastructure.persistence.venta.jpa;

import com.playzone.pems.infrastructure.persistence.venta.entity.VentaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VentaJpaRepository extends JpaRepository<VentaEntity, Long> {

    Page<VentaEntity> findBySede_IdAndFechaVentaBetween(
            Long idSede, LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    Page<VentaEntity> findByUsuario_Id(Long idUsuario, Pageable pageable);
}