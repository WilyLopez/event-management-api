package com.playzone.pems.infrastructure.persistence.inventario.adapter;

import com.playzone.pems.domain.inventario.model.MovimientoInventario;
import com.playzone.pems.domain.inventario.model.enums.TipoMovimiento;
import com.playzone.pems.domain.inventario.repository.MovimientoInventarioRepository;
import com.playzone.pems.infrastructure.persistence.inventario.jpa.MovimientoInventarioJpaRepository;
import com.playzone.pems.infrastructure.persistence.inventario.jpa.ProductoJpaRepository;
import com.playzone.pems.infrastructure.persistence.inventario.mapper.ProductoEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MovimientoInventarioPersistenceAdapter implements MovimientoInventarioRepository {

    private final MovimientoInventarioJpaRepository movimientoJpa;
    private final ProductoJpaRepository             productoJpa;
    private final UsuarioAdminJpaRepository         adminJpa;
    private final ProductoEntityMapper              mapper;

    @Override
    public Optional<MovimientoInventario> findById(Long id) {
        return movimientoJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<MovimientoInventario> findByProducto(Long idProducto, Pageable pageable) {
        return movimientoJpa.findByProducto_IdOrderByFechaMovimientoDesc(idProducto, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<MovimientoInventario> findByProductoAndTipoAndFechas(Long idProducto,
                                                                     TipoMovimiento tipo,
                                                                     LocalDateTime desde,
                                                                     LocalDateTime hasta,
                                                                     Pageable pageable) {
        return movimientoJpa.findByProducto_IdAndTipoMovimientoAndFechaMovimientoBetween(
                idProducto, tipo, desde, hasta, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public MovimientoInventario save(MovimientoInventario mov) {
        var producto = productoJpa.findById(mov.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", mov.getIdProducto()));
        var usuario  = mov.getIdUsuario() != null
                ? adminJpa.findById(mov.getIdUsuario()).orElse(null) : null;
        return mapper.toDomain(movimientoJpa.save(mapper.toEntity(mov, producto, usuario)));
    }
}