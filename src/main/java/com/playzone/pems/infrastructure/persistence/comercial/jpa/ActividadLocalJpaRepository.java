package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.ActividadLocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadLocalJpaRepository extends JpaRepository<ActividadLocalEntity, Long> {
    List<ActividadLocalEntity> findByActivaTrueOrderByOrdenAsc();
    List<ActividadLocalEntity> findByEsEspecialTrueAndActivaTrue();
    List<ActividadLocalEntity> findByZona_Id(Long idZona);
}
