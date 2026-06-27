package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.GastoEventoPrivado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface GastoEventoPrivadoRepository {
    List<GastoEventoPrivado> findByEvento(Long idEvento);
    List<GastoEventoPrivado> findBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin);
    BigDecimal sumMontoByEvento(Long idEvento);
    Map<Long, BigDecimal> sumMontoByEventoIds(List<Long> ids);
    GastoEventoPrivado save(GastoEventoPrivado gasto);
    void deleteById(Long id);
}
