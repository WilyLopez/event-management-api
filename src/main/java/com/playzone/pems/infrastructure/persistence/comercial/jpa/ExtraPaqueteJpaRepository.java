package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.ExtraPaqueteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExtraPaqueteJpaRepository extends JpaRepository<ExtraPaqueteEntity, Long> {

    List<ExtraPaqueteEntity> findByPaquete_IdAndActivoTrueOrderByOrdenAsc(Long idPaquete);

    List<ExtraPaqueteEntity> findByPaquete_IdOrderByOrdenAsc(Long idPaquete);

    @Modifying
    @Query("UPDATE ExtraPaqueteEntity e SET e.activo = false WHERE e.id = :id")
    void desactivar(@Param("id") Long id);
}
