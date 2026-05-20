package com.playzone.pems.infrastructure.persistence.contrato.jpa;

import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ContratoProveedorJpaRepository extends JpaRepository<ContratoProveedorEntity, Long> {

    List<ContratoProveedorEntity> findByContrato_Id(Long idContrato);

    boolean existsByContrato_IdAndProveedor_Id(Long idContrato, Long idProveedor);

    @Query("SELECT COALESCE(SUM(cp.montoAcordado), 0) FROM ContratoProveedorEntity cp " +
           "WHERE cp.contrato.eventoPrivado.id = :idEvento AND cp.contratadoPor = 'EMPRESA'")
    BigDecimal sumMontoAcordadoByEventoAndContratadoPorEmpresa(@Param("idEvento") Long idEvento);
}