package com.playzone.pems.domain.venta.repository;

import com.playzone.pems.domain.venta.model.DetalleVenta;

import java.util.List;
import java.util.Optional;

public interface DetalleVentaRepository {

    Optional<DetalleVenta> findById(Long id);

    List<DetalleVenta> findByVenta(Long idVenta);

    DetalleVenta save(DetalleVenta detalle);

    List<DetalleVenta> saveAll(List<DetalleVenta> detalles);
}