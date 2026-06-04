package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.infrastructure.persistence.evento.entity.PagoVentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoVentaJpaRepository extends JpaRepository<PagoVentaEntity, Long> {

    List<PagoVentaEntity> findByVenta_Id(Long idVenta);
}
