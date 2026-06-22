package com.playzone.pems.infrastructure.persistence.comercial.jpa;

import com.playzone.pems.infrastructure.persistence.comercial.entity.BeneficioPaqueteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficioPaqueteJpaRepository extends JpaRepository<BeneficioPaqueteEntity, Long> {
    List<BeneficioPaqueteEntity> findByPaquete_IdOrderByOrdenAsc(Long idPaquete);
    List<BeneficioPaqueteEntity> findByPaquete_IdInOrderByOrdenAsc(List<Long> idsPaquetes);
    void deleteByPaquete_Id(Long idPaquete);
}
