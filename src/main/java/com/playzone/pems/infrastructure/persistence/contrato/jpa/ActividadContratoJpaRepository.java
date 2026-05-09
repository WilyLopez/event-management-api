package com.playzone.pems.infrastructure.persistence.contrato.jpa;

import com.playzone.pems.infrastructure.persistence.contrato.entity.ActividadContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadContratoJpaRepository
        extends JpaRepository<ActividadContratoEntity, Long> {

    List<ActividadContratoEntity> findByContrato_IdOrderByFechaAccionDesc(Long idContrato);
}