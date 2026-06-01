package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.ServicioCotizacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioCotizacionJpaRepository extends JpaRepository<ServicioCotizacionEntity, Long> {
    List<ServicioCotizacionEntity> findByActivoTrueOrderByOrdenAsc();
}
