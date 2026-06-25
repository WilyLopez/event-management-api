package com.playzone.pems.infrastructure.persistence.contrato.jpa;

import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ContratoJpaRepository extends JpaRepository<ContratoEntity, Long> {

    Optional<ContratoEntity> findFirstByEventoPrivado_IdOrderByIdDesc(Long idEventoPrivado);

    boolean existsByEventoPrivado_Id(Long idEventoPrivado);

    @Query("SELECT c FROM ContratoEntity c " +
           "JOIN c.eventoPrivado e " +
           "WHERE (:estado IS NULL OR c.estado = :estado) " +
           "AND (:idSede IS NULL OR e.sede.id = :idSede) " +
           "AND (CAST(:fecha AS localdate) IS NULL OR e.fechaEvento = :fecha)")
    Page<ContratoEntity> buscarConFiltros(
            @Param("search") String search,
            @Param("estado") EstadoContrato estado,
            @Param("idSede") Long idSede,
            @Param("fecha") LocalDate fecha,
            Pageable pageable);
}