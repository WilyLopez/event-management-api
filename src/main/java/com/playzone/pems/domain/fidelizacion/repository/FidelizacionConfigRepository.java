package com.playzone.pems.domain.fidelizacion.repository;

import com.playzone.pems.domain.fidelizacion.model.FidelizacionConfig;
import java.util.Optional;

public interface FidelizacionConfigRepository {
    Optional<FidelizacionConfig> findByIdSede(Long idSede);
    FidelizacionConfig save(FidelizacionConfig config);
}
