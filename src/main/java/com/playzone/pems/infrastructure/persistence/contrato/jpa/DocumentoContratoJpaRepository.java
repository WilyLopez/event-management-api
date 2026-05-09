package com.playzone.pems.infrastructure.persistence.contrato.jpa;

import com.playzone.pems.infrastructure.persistence.contrato.entity.DocumentoContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoContratoJpaRepository
        extends JpaRepository<DocumentoContratoEntity, Long> {

    List<DocumentoContratoEntity> findByContrato_Id(Long idContrato);
}