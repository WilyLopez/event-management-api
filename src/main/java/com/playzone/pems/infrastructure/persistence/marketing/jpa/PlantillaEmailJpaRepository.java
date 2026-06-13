package com.playzone.pems.infrastructure.persistence.marketing.jpa;

import com.playzone.pems.infrastructure.persistence.marketing.entity.PlantillaEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantillaEmailJpaRepository extends JpaRepository<PlantillaEmailEntity, Long> {

    List<PlantillaEmailEntity> findByTipoEmailCodigoAndEsActivaTrue(String tipoEmailCodigo);
}
