package com.playzone.pems.infrastructure.persistence.inventario.jpa;

import com.playzone.pems.domain.inventario.model.enums.TipoMovimiento;
import com.playzone.pems.infrastructure.persistence.inventario.entity.MovimientoInventarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MovimientoInventarioJpaRepository extends JpaRepository<MovimientoInventarioEntity, Long> {

    Page<MovimientoInventarioEntity> findByProducto_IdOrderByFechaMovimientoDesc(Long idProducto, Pageable pageable);

    Page<MovimientoInventarioEntity> findByProducto_IdAndTipoMovimientoAndFechaMovimientoBetween(
            Long idProducto, TipoMovimiento tipo, LocalDateTime desde, LocalDateTime hasta, Pageable pageable);
}