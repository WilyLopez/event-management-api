package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.ContenidoLegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContenidoLegalJpaRepository extends JpaRepository<ContenidoLegalEntity, Long> {

    Optional<ContenidoLegalEntity> findByTipoAndActivoTrue(String tipo);
}
