package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.SeccionWebEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeccionWebJpaRepository extends JpaRepository<SeccionWebEntity, Long> {

    Optional<SeccionWebEntity> findByCodigo(String codigo);

    List<SeccionWebEntity> findByVisibleTrueOrderByOrdenVisualizacionAsc();

    boolean existsByCodigo(String codigo);
}
