package com.playzone.pems.infrastructure.persistence.contrato.jpa;

import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContratoJpaRepository extends JpaRepository<ContratoEntity, Long> {

    Optional<ContratoEntity> findByEventoPrivado_Id(Long idEventoPrivado);

    boolean existsByEventoPrivado_Id(Long idEventoPrivado);
}