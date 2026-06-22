package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.TipoContenidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoContenidoJpaRepository extends JpaRepository<TipoContenidoEntity, String> {
}
