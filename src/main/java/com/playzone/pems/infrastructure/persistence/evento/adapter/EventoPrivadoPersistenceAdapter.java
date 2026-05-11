package com.playzone.pems.infrastructure.persistence.evento.adapter;

import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.domain.evento.repository.EventoPrivadoRepository;
import com.playzone.pems.infrastructure.persistence.calendario.jpa.TurnoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.mapper.EventoPrivadoEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.ClienteJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventoPrivadoPersistenceAdapter implements EventoPrivadoRepository {

    private final EventoPrivadoJpaRepository eventoJpa;
    private final ClienteJpaRepository       clienteJpa;
    private final SedeJpaRepository          sedeJpa;
    private final TurnoJpaRepository         turnoJpa;
    private final UsuarioAdminJpaRepository  adminJpa;
    private final EventoPrivadoEntityMapper  mapper;

    @Override public Optional<EventoPrivado> findById(Long id) {
        return eventoJpa.findById(id).map(mapper::toDomain);
    }

    @Override public Page<EventoPrivado> findByCliente(Long idCliente, Pageable pageable) {
        return eventoJpa.findByCliente_Id(idCliente, pageable).map(mapper::toDomain);
    }

    @Override public Page<EventoPrivado> findBySedeAndEstado(Long idSede, EstadoEventoPrivado estado, Pageable pageable) {
        return eventoJpa.findBySede_IdAndEstado(idSede, estado, pageable).map(mapper::toDomain);
    }

    @Override public Page<EventoPrivado> findBySedeAndFechasBetween(Long idSede, LocalDate inicio, LocalDate fin, Pageable pageable) {
        return eventoJpa.findBySede_IdAndFechaEventoBetween(idSede, inicio, fin, pageable).map(mapper::toDomain);
    }

    @Override public List<EventoPrivado> findBySedeAndFechaBetween(Long idSede, LocalDate inicio, LocalDate fin) {
        return eventoJpa.findBySede_IdAndFechaEventoBetween(idSede, inicio, fin).stream().map(mapper::toDomain).toList();
    }

    @Override public List<EventoPrivado> findBySedeAndFecha(Long idSede, LocalDate fecha) {
        return eventoJpa.findBySede_IdAndFechaEvento(idSede, fecha).stream().map(mapper::toDomain).toList();
    }

    @Override public boolean existsActivoBySedeAndFechaAndTurno(Long idSede, LocalDate fecha, Long idTurno) {
        return eventoJpa.existsActivoBySedeAndFechaAndTurno(idSede, fecha, idTurno);
    }

    @Override
    public Page<EventoPrivado> buscarAdmin(
            Long idSede, EstadoEventoPrivado estadoEnum, LocalDate fecha, String searchPattern, Pageable pageable) {
        return eventoJpa.buscarAdmin(idSede, estadoEnum, fecha, searchPattern, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public EventoPrivado save(EventoPrivado evento) {
        var cliente = clienteJpa.findById(evento.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", evento.getIdCliente()));
        var sede = sedeJpa.findById(evento.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", evento.getIdSede()));
        var turno = turnoJpa.findById(evento.getIdTurno())
                .orElseThrow(() -> new ResourceNotFoundException("Turno", evento.getIdTurno()));
        var gestor = evento.getIdUsuarioGestor() != null
                ? adminJpa.findById(evento.getIdUsuarioGestor()).orElse(null) : null;

        return mapper.toDomain(eventoJpa.save(mapper.toEntity(evento, cliente, sede, turno, gestor)));
    }
}