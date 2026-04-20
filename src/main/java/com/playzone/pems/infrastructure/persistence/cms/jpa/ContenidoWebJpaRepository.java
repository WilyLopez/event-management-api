package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.ContenidoWebEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContenidoWebJpaRepository extends JpaRepository<ContenidoWebEntity, Long> {

    Optional<ContenidoWebEntity> findByIdSeccionAndClave(Long idSeccion, String clave);

    List<ContenidoWebEntity> findByIdSeccionAndActivoTrue(Long idSeccion);

    List<ContenidoWebEntity> findByActivoTrue();

    boolean existsByIdSeccionAndClave(Long idSeccion, String clave);
}