package com.playzone.pems.domain.finanzas.repository;

import com.playzone.pems.domain.finanzas.model.AperturaCaja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AperturaCajaRepository {
    Optional<AperturaCaja> findById(Long id);
    Optional<AperturaCaja> findBySedeAndFecha(Long idSede, LocalDate fecha);
    Optional<AperturaCaja> findActivaBySede(Long idSede);
    Optional<AperturaCaja> findAbiertaBySede(Long idSede);
    List<AperturaCaja> findBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin);
    AperturaCaja save(AperturaCaja apertura);
    void incrementarIngresos(Long id, BigDecimal delta);
    void incrementarEgresos(Long id, BigDecimal delta);
}
