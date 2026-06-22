package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.PresupuestoEvento;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PresupuestoEventoRepository {
    Optional<PresupuestoEvento> findById(Long id);
    List<PresupuestoEvento> findByEvento(Long idEventoPrivado);
    BigDecimal sumMontoEstimadoByEvento(Long idEventoPrivado);
    BigDecimal sumMontoRealByEvento(Long idEventoPrivado);
    PresupuestoEvento save(PresupuestoEvento presupuesto);
    void deleteById(Long id);
}
