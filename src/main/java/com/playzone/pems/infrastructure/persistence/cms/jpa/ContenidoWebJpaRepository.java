package com.playzone.pems.infrastructure.persistence.cms.jpa;

import com.playzone.pems.infrastructure.persistence.cms.entity.ContenidoWebEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContenidoWebJpaRepository extends JpaRepository<ContenidoWebEntity, Long> {

    Optional<ContenidoWebEntity> findByIdSeccionAndClave(Long idSeccion, String clave);

    List<ContenidoWebEntity> findByIdSeccionAndActivoTrue(Long idSeccion);

    List<ContenidoWebEntity> findByActivoTrue();

    @Query("SELECT c FROM ContenidoWebEntity c " +
           "WHERE (:idSeccion IS NULL OR c.idSeccion = :idSeccion) " +
           "AND (:clavePattern IS NULL OR LOWER(c.clave) LIKE :clavePattern)")
    Page<ContenidoWebEntity> findByFilters(
            @Param("idSeccion") Long idSeccion,
            @Param("clavePattern") String clavePattern,
            Pageable pageable);

    boolean existsByIdSeccionAndClave(Long idSeccion, String clave);
}
