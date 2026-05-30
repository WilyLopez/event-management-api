package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.infrastructure.persistence.finanzas.entity.PresupuestoEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PresupuestoEventoJpaRepository extends JpaRepository<PresupuestoEventoEntity, Long> {

    List<PresupuestoEventoEntity> findByEventoPrivado_IdOrderByFechaCreacionAsc(Long idEventoPrivado);

    @Query("SELECT COALESCE(SUM(p.montoEstimado), 0) FROM PresupuestoEventoEntity p " +
           "WHERE p.eventoPrivado.id = :idEvento")
    BigDecimal sumMontoEstimadoByEvento(@Param("idEvento") Long idEvento);

    @Query("SELECT COALESCE(SUM(p.montoReal), 0) FROM PresupuestoEventoEntity p " +
           "WHERE p.eventoPrivado.id = :idEvento AND p.montoReal IS NOT NULL")
    BigDecimal sumMontoRealByEvento(@Param("idEvento") Long idEvento);
}
