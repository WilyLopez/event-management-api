package com.playzone.pems.infrastructure.persistence.marketing.jpa;

import com.playzone.pems.infrastructure.persistence.marketing.entity.EnvioEmailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnvioEmailJpaRepository extends JpaRepository<EnvioEmailEntity, Long> {

    Page<EnvioEmailEntity> findByIdCampanaEmail(Long idCampanaEmail, Pageable pageable);

    @Query("SELECT e FROM EnvioEmailEntity e WHERE e.idCampanaEmail = :idCampana AND e.estado = 'PENDIENTE' ORDER BY e.id")
    List<EnvioEmailEntity> findPendientesByCampana(
            @Param("idCampana") Long idCampana,
            Pageable pageable);

    @Query("SELECT e FROM EnvioEmailEntity e WHERE e.estado = 'ERROR' AND e.intentos < :maxIntentos ORDER BY e.fechaCreacion")
    List<EnvioEmailEntity> findParaReintentar(@Param("maxIntentos") int maxIntentos, Pageable pageable);

    long countByIdCampanaEmailAndEstado(Long idCampanaEmail, String estado);
}
