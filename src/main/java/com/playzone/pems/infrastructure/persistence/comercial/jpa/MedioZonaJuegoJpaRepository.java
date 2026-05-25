package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.MedioZonaJuegoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedioZonaJuegoJpaRepository extends JpaRepository<MedioZonaJuegoEntity, Long> {
    List<MedioZonaJuegoEntity> findByZona_IdOrderByOrdenAsc(Long idZona);
    void deleteByZona_Id(Long idZona);
    Optional<MedioZonaJuegoEntity> findByZona_IdAndUrl(Long idZona, String url);
}
