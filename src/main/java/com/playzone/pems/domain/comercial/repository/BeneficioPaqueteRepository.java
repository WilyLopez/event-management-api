package com.playzone.pems.domain.comercial.repository;

import com.playzone.pems.domain.comercial.model.BeneficioPaquete;
import java.util.List;
import java.util.Optional;

public interface BeneficioPaqueteRepository {
    List<BeneficioPaquete> findByPaquete(Long idPaquete);
    Optional<BeneficioPaquete> findById(Long id);
    BeneficioPaquete save(BeneficioPaquete beneficio);
    void deleteById(Long id);
}
