package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.PaqueteEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaqueteEventoJpaRepository extends JpaRepository<PaqueteEventoEntity, Long> {
    List<PaqueteEventoEntity> findByEsActivoTrueAndDeletedAtIsNullOrderByOrdenAsc();
    List<PaqueteEventoEntity> findByDeletedAtIsNullOrderByOrdenAsc();
    boolean existsBySlugAndDeletedAtIsNull(String slug);
    Optional<PaqueteEventoEntity> findBySlugAndDeletedAtIsNull(String slug);
}
