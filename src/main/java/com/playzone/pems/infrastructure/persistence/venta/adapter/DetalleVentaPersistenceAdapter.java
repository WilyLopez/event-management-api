package com.playzone.pems.infrastructure.persistence.venta.adapter;

import com.playzone.pems.domain.venta.model.DetalleVenta;
import com.playzone.pems.domain.venta.repository.DetalleVentaRepository;
import com.playzone.pems.infrastructure.persistence.inventario.jpa.ProductoJpaRepository;
import com.playzone.pems.infrastructure.persistence.venta.jpa.DetalleVentaJpaRepository;
import com.playzone.pems.infrastructure.persistence.venta.jpa.VentaJpaRepository;
import com.playzone.pems.infrastructure.persistence.venta.mapper.VentaEntityMapper;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DetalleVentaPersistenceAdapter implements DetalleVentaRepository {

    private final DetalleVentaJpaRepository detalleJpa;
    private final VentaJpaRepository        ventaJpa;
    private final ProductoJpaRepository     productoJpa;
    private final VentaEntityMapper         mapper;

    @Override
    public Optional<DetalleVenta> findById(Long id) {
        return detalleJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<DetalleVenta> findByVenta(Long idVenta) {
        return detalleJpa.findByVenta_Id(idVenta).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public DetalleVenta save(DetalleVenta detalle) {
        var venta    = ventaJpa.findById(detalle.getIdVenta())
                .orElseThrow(() -> new ResourceNotFoundException("Venta", detalle.getIdVenta()));
        var producto = productoJpa.findById(detalle.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", detalle.getIdProducto()));
        return mapper.toDomain(detalleJpa.save(mapper.toEntity(detalle, venta, producto)));
    }

    @Override
    @Transactional
    public List<DetalleVenta> saveAll(List<DetalleVenta> detalles) {
        return detalles.stream().map(this::save).toList();
    }
}