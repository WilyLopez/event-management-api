package com.playzone.pems.infrastructure.persistence.calendario.jpa;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.infrastructure.persistence.calendario.entity.TarifaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TarifaJpaRepository extends JpaRepository<TarifaEntity, Long> {

    @Query("SELECT t FROM TarifaEntity t WHERE t.sede.id = :idSede AND t.tipoDia = :tipoDia AND t.activo = true AND t.vigenciaDesde <= :fecha AND (t.vigenciaHasta IS NULL OR t.vigenciaHasta >= :fecha)")
    Optional<TarifaEntity> findVigenteBySedeAndTipoDiaAndFecha(
            @Param("idSede") Long idSede,
            @Param("tipoDia") TipoDia tipoDia,
            @Param("fecha") LocalDate fecha);

    List<TarifaEntity> findBySede_IdAndActivoTrueOrderByVigenciaDesdeDesc(Long idSede);

    @Modifying
    @Query("UPDATE TarifaEntity t SET t.activo = false WHERE t.sede.id = :idSede AND t.tipoDia = :tipoDia AND t.activo = true")
    void desactivarAnterioresBySedeAndTipoDia(@Param("idSede") Long idSede, @Param("tipoDia") TipoDia tipoDia);
}