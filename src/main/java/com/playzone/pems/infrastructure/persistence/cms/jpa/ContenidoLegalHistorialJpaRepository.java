package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.ContenidoLegalHistorialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenidoLegalHistorialJpaRepository
        extends JpaRepository<ContenidoLegalHistorialEntity, Long> {

    List<ContenidoLegalHistorialEntity> findByTipoOrderByVersionDesc(String tipo);
}
