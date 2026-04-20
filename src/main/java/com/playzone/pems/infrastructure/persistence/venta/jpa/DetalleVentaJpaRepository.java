package com.playzone.pems.infrastructure.persistence.venta.jpa;

import com.playzone.pems.infrastructure.persistence.venta.entity.DetalleVentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaJpaRepository extends JpaRepository<DetalleVentaEntity, Long> {

    List<DetalleVentaEntity> findByVenta_Id(Long idVenta);
}