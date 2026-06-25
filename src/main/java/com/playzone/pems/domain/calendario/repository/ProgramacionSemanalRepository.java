package com.playzone.pems.domain.calendario.repository;

import com.playzone.pems.domain.calendario.model.ProgramacionSemanal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProgramacionSemanalRepository {

    ProgramacionSemanal guardar(ProgramacionSemanal programacion);

    Optional<ProgramacionSemanal> findById(Long id);

    /** Programaciones activas cuyo rango solapa con [inicio, fin]. */
    List<ProgramacionSemanal> findActivasBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin);

    /** Programaciones activas con semanaFin >= hoy (incluye semana actual y futuras). */
    List<ProgramacionSemanal> findActivasFuturasBySede(Long idSede);

    /** ¿Existe alguna programacion activa que cubra esta fecha? */
    boolean existeActivaEnFecha(Long idSede, LocalDate fecha);

    /** ¿Existe solapamiento activo con el rango dado? */
    boolean existeSolapamiento(Long idSede, LocalDate inicio, LocalDate fin);

    /** IDs de sedes que NO tienen programacion activa en la semana [inicio, fin]. */
    List<Long> findSedeIdsSinProgramacionEnSemana(LocalDate inicio, LocalDate fin);

    void cancelar(Long id);
}
