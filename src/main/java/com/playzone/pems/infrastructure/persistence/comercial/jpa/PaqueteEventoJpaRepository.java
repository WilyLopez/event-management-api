package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.PaqueteEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaqueteEventoJpaRepository extends JpaRepository<PaqueteEventoEntity, Long> {
    List<PaqueteEventoEntity> findByActivoTrueOrderByOrdenAsc();
    List<PaqueteEventoEntity> findAllByOrderByOrdenAsc();
    boolean existsBySlug(String slug);
    Optional<PaqueteEventoEntity> findBySlug(String slug);
}
