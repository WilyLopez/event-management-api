package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.ZonaJuegoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaJuegoJpaRepository extends JpaRepository<ZonaJuegoEntity, Long> {
    List<ZonaJuegoEntity> findByActivaTrueOrderByOrdenAsc();
    List<ZonaJuegoEntity> findAllByOrderByOrdenAsc();
    boolean existsBySlug(String slug);
}
