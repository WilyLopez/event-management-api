package com.playzone.pems.infrastructure.persistence.contrato.jpa;

import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContratoJpaRepository extends JpaRepository<ContratoEntity, Long> {

    Optional<ContratoEntity> findByEventoPrivado_Id(Long idEventoPrivado);

    boolean existsByEventoPrivado_Id(Long idEventoPrivado);

    @Query("SELECT c FROM ContratoEntity c " +
           "JOIN c.eventoPrivado e " +
           "JOIN e.cliente cl " +
           "WHERE (CAST(:search AS string) IS NULL OR LOWER(cl.nombre) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(cl.correo) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (:estado IS NULL OR c.estado = :estado) " +
           "AND (:idSede IS NULL OR e.sede.id = :idSede)")
    Page<ContratoEntity> buscarConFiltros(
            @Param("search") String search,
            @Param("estado") EstadoContrato estado,
            @Param("idSede") Long idSede,
            Pageable pageable);
}