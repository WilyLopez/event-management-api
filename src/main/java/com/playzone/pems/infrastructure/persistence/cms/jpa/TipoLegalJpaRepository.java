package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.TipoLegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoLegalJpaRepository extends JpaRepository<TipoLegalEntity, String> {

    Optional<TipoLegalEntity> findBySlug(String slug);

    List<TipoLegalEntity> findAllByOrderByOrdenAsc();
}
