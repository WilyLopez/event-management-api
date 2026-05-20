package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.GastoOperativoDiario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GastoOperativoDiarioRepository {
    List<GastoOperativoDiario> findBySedeAndFecha(Long idSede, LocalDate fecha);
    List<GastoOperativoDiario> findBySedeAndRangoFecha(Long idSede, LocalDate inicio, LocalDate fin);
    BigDecimal sumMontoBySedeAndFecha(Long idSede, LocalDate fecha);
    BigDecimal sumMontoBySedeAndPeriodo(Long idSede, int anio, int mes);
    GastoOperativoDiario save(GastoOperativoDiario gasto);
    void deleteById(Long id);
}
