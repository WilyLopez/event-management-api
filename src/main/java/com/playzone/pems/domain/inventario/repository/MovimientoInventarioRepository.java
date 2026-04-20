package com.playzone.pems.domain.inventario.repository;

import com.playzone.pems.domain.inventario.model.MovimientoInventario;
import com.playzone.pems.domain.inventario.model.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MovimientoInventarioRepository {

    Optional<MovimientoInventario> findById(Long id);

    Page<MovimientoInventario> findByProducto(Long idProducto, Pageable pageable);

    Page<MovimientoInventario> findByProductoAndTipoAndFechas(
            Long idProducto,
            TipoMovimiento tipo,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable);

    MovimientoInventario save(MovimientoInventario movimiento);
}