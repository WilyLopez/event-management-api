package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.GastoEventoPrivado;

import java.math.BigDecimal;
import java.util.List;

public interface GastoEventoPrivadoRepository {
    List<GastoEventoPrivado> findByEvento(Long idEvento);
    BigDecimal sumMontoByEvento(Long idEvento);
    GastoEventoPrivado save(GastoEventoPrivado gasto);
    void deleteById(Long id);
}
