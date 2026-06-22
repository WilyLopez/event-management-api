package com.playzone.pems.domain.calendario.repository;

import com.playzone.pems.domain.calendario.model.BloqueCalendario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BloqueCalendarioRepository {

    Optional<BloqueCalendario> findById(Long id);

    List<BloqueCalendario> findActivosBySede(Long idSede);

    List<BloqueCalendario> findActivosBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin);

    boolean existsBloqueActivoEnFecha(Long idSede, LocalDate fecha);

    boolean existsBloqueEfectivoEnFecha(Long idSede, LocalDate fecha);

    boolean existsSolapamientoEnRango(Long idSede, LocalDate inicio, LocalDate fin);

    BloqueCalendario save(BloqueCalendario bloque);

    void desactivar(Long id);
}