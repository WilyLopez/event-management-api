package com.playzone.pems.infrastructure.persistence.facturacion.jpa;

import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import com.playzone.pems.infrastructure.persistence.facturacion.entity.SerieComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SerieComprobanteJpaRepository extends JpaRepository<SerieComprobanteEntity, Long> {

    Optional<SerieComprobanteEntity> findBySede_IdAndTipoComprobanteAndActivoTrue(
            Long idSede, TipoComprobante tipo);

    @Modifying
    @Query("UPDATE SerieComprobanteEntity s SET s.correlativoActual = s.correlativoActual + 1 WHERE s.id = :id")
    void incrementarCorrelativo(@Param("id") Long id);

    @Query("SELECT s.correlativoActual FROM SerieComprobanteEntity s WHERE s.id = :id")
    int findCorrelativoActual(@Param("id") Long id);
}