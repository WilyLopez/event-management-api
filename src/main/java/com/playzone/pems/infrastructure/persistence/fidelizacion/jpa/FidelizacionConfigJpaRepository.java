package com.playzone.pems.infrastructure.persistence.fidelizacion.jpa;

import com.playzone.pems.infrastructure.persistence.fidelizacion.entity.FidelizacionConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FidelizacionConfigJpaRepository extends JpaRepository<FidelizacionConfigEntity, Long> {
    Optional<FidelizacionConfigEntity> findByIdSede(Long idSede);
}
