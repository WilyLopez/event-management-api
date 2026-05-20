package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.infrastructure.persistence.finanzas.entity.GastoEventoPrivadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface GastoEventoPrivadoJpaRepository extends JpaRepository<GastoEventoPrivadoEntity, Long> {

    List<GastoEventoPrivadoEntity> findByEventoPrivado_Id(Long idEvento);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM GastoEventoPrivadoEntity g WHERE g.eventoPrivado.id = :idEvento")
    BigDecimal sumMontoByEvento(@Param("idEvento") Long idEvento);
}
