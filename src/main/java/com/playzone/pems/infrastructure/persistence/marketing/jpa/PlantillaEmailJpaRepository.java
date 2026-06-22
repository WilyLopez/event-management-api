package com.playzone.pems.infrastructure.persistence.marketing.jpa;

import com.playzone.pems.infrastructure.persistence.marketing.entity.PlantillaEmailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface PlantillaEmailJpaRepository extends JpaRepository<PlantillaEmailEntity, Long> {

    List<PlantillaEmailEntity> findByTipoEmailCodigoAndEsActivaTrue(String tipoEmailCodigo);

    @Query("SELECT p FROM PlantillaEmailEntity p WHERE p.deletedAt IS NULL")
    Page<PlantillaEmailEntity> findAllActivas(Pageable pageable);

    @Query("""
            SELECT p FROM PlantillaEmailEntity p
            WHERE p.deletedAt IS NULL
              AND p.tipoEmailCodigo IN (
                  SELECT t.codigo FROM TipoEmailEntity t
                  WHERE t.esSistema = false AND t.activo = true
              )
            """)
    Page<PlantillaEmailEntity> findAllMarketing(Pageable pageable);

    @Modifying
    @Query("UPDATE PlantillaEmailEntity p SET p.deletedAt = :ahora WHERE p.id = :id")
    void softDelete(@Param("id") Long id, @Param("ahora") OffsetDateTime ahora);

    @Modifying
    @Query("UPDATE PlantillaEmailEntity p SET p.esActiva = :activa, p.updatedAt = :ahora WHERE p.id = :id")
    void actualizarEstado(@Param("id") Long id, @Param("activa") boolean activa, @Param("ahora") OffsetDateTime ahora);
}
