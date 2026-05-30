package com.playzone.pems.domain.comercial.repository;

import com.playzone.pems.domain.comercial.model.ExtraPaquete;

import java.util.List;
import java.util.Optional;

public interface ExtraPaqueteRepository {

    List<ExtraPaquete> findActivosByPaquete(Long idPaquete);

    List<ExtraPaquete> findByPaquete(Long idPaquete);

    Optional<ExtraPaquete> findById(Long id);

    ExtraPaquete save(ExtraPaquete extra);

    void desactivar(Long id);
}
