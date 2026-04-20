package com.playzone.pems.domain.calendario.repository;

import com.playzone.pems.domain.calendario.model.Tarifa;
import com.playzone.pems.domain.calendario.model.enums.TipoDia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TarifaRepository {

    Optional<Tarifa> findById(Long id);

    Optional<Tarifa> findVigenteBySedeAndTipoDiaAndFecha(
            Long idSede, TipoDia tipoDia, LocalDate fecha);

    List<Tarifa> findActivasBySede(Long idSede);

    Tarifa save(Tarifa tarifa);

    void desactivarAnterioresBySedeAndTipoDia(Long idSede, TipoDia tipoDia);
}