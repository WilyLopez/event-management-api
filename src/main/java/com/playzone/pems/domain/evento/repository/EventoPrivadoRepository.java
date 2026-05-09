package com.playzone.pems.domain.evento.repository;

import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventoPrivadoRepository {

    Optional<EventoPrivado> findById(Long id);

    Page<EventoPrivado> findByCliente(Long idCliente, Pageable pageable);

    Page<EventoPrivado> findBySedeAndEstado(
            Long idSede, EstadoEventoPrivado estado, Pageable pageable);

    Page<EventoPrivado> findBySedeAndFechasBetween(
            Long idSede, LocalDate inicio, LocalDate fin, Pageable pageable);

    List<EventoPrivado> findBySedeAndFecha(Long idSede, LocalDate fecha);

    boolean existsActivoBySedeAndFechaAndTurno(Long idSede, LocalDate fecha, Long idTurno);

    EventoPrivado save(EventoPrivado evento);
}