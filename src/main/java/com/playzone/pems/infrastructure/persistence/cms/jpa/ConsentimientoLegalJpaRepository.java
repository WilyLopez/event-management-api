package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.ConsentimientoLegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentimientoLegalJpaRepository
        extends JpaRepository<ConsentimientoLegalEntity, Long> {
}
