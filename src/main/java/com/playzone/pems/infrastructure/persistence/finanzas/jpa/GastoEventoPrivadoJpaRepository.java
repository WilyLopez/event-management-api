package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.infrastructure.persistence.finanzas.entity.GastoEventoPrivadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface GastoEventoPrivadoJpaRepository extends JpaRepository<GastoEventoPrivadoEntity, Long> {

    List<GastoEventoPrivadoEntity> findByEventoId(Long eventoId);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM GastoEventoPrivadoEntity g WHERE g.eventoId = :eventoId")
    BigDecimal sumMontoByEvento(@Param("eventoId") Long eventoId);

    @Query("SELECT g.eventoId, COALESCE(SUM(g.monto), 0) FROM GastoEventoPrivadoEntity g " +
           "WHERE g.eventoId IN :ids GROUP BY g.eventoId")
    List<Object[]> sumMontoByEventoIds(@Param("ids") List<Long> ids);
}
