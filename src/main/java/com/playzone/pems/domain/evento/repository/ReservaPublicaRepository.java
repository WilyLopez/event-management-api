package com.playzone.pems.domain.evento.repository;

import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaPublicaRepository {

    Optional<ReservaPublica> findById(Long id);

    Optional<ReservaPublica> findByNumeroTicket(String numeroTicket);

    Page<ReservaPublica> findByCliente(Long idCliente, Pageable pageable);

    Page<ReservaPublica> findBySedeAndFecha(Long idSede, LocalDate fecha, Pageable pageable);

    List<ReservaPublica> findBySedeAndFecha(Long idSede, LocalDate fecha);

    Page<ReservaPublica> findBySedeAndEstado(
            Long idSede, EstadoReservaPublica estado, Pageable pageable);

    List<ReservaPublica> findConfirmadasBySedeAndFecha(Long idSede, LocalDate fecha);

    int countConfirmadasBySedeAndFecha(Long idSede, LocalDate fecha);

    ReservaPublica save(ReservaPublica reserva);

    boolean existsByNumeroTicket(String numeroTicket);
}