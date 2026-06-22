package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.RegistroIngreso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RegistroIngresoRepository {
    Optional<RegistroIngreso> findById(Long id);
    Page<RegistroIngreso> findBySede(Long idSede, Pageable pageable);
    List<RegistroIngreso> findBySedeAndRangoFecha(Long idSede, LocalDate inicio, LocalDate fin);
    List<RegistroIngreso> findBySedeAndPeriodo(Long idSede, int anio, int mes);
    BigDecimal sumMontoBySedeAndPeriodo(Long idSede, int anio, int mes);
    BigDecimal sumMontoBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin);
    Map<String, BigDecimal> sumMontoAgrupadoPorTipo(Long idSede, int anio, int mes);
    RegistroIngreso save(RegistroIngreso registro);
    void deleteById(Long id);
}
