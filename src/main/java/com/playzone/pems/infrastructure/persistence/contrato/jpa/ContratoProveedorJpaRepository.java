package com.playzone.pems.infrastructure.persistence.contrato.jpa;

import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoProveedorJpaRepository extends JpaRepository<ContratoProveedorEntity, Long> {

    List<ContratoProveedorEntity> findByContrato_Id(Long idContrato);

    boolean existsByContrato_IdAndProveedor_Id(Long idContrato, Long idProveedor);
}