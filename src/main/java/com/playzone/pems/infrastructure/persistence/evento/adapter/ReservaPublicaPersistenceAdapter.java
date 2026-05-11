package com.playzone.pems.infrastructure.persistence.evento.adapter;

import com.playzone.pems.application.evento.dto.query.MetricasReservaQuery;
import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.domain.evento.repository.ReservaPublicaRepository;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ReservaPublicaJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.mapper.ReservaPublicaEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.ClienteJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservaPublicaPersistenceAdapter implements ReservaPublicaRepository {

    private final ReservaPublicaJpaRepository reservaJpa;
    private final ClienteJpaRepository        clienteJpa;
    private final SedeJpaRepository           sedeJpa;
    private final ReservaPublicaEntityMapper  mapper;

    @Override public Optional<ReservaPublica> findById(Long id) {
        return reservaJpa.findById(id).map(mapper::toDomain);
    }

    @Override public Optional<ReservaPublica> findByNumeroTicket(String ticket) {
        return reservaJpa.findByNumeroTicket(ticket).map(mapper::toDomain);
    }

    @Override public Page<ReservaPublica> findByCliente(Long idCliente, Pageable pageable) {
        return reservaJpa.findByCliente_Id(idCliente, pageable).map(mapper::toDomain);
    }

    @Override public Page<ReservaPublica> findBySedeAndFecha(Long idSede, LocalDate fecha, Pageable pageable) {
        return reservaJpa.findBySede_IdAndFechaEvento(idSede, fecha, pageable).map(mapper::toDomain);
    }

    @Override public List<ReservaPublica> findBySedeAndFecha(Long idSede, LocalDate fecha) {
        return reservaJpa.findBySede_IdAndFechaEvento(idSede, fecha).stream().map(mapper::toDomain).toList();
    }

    @Override public List<ReservaPublica> findBySedeAndFechaBetween(Long idSede, LocalDate inicio, LocalDate fin) {
        return reservaJpa.findBySede_IdAndFechaEventoBetween(idSede, inicio, fin).stream().map(mapper::toDomain).toList();
    }

    @Override public Page<ReservaPublica> findBySedeAndEstado(Long idSede, EstadoReservaPublica estado, Pageable pageable) {
        return reservaJpa.findBySede_IdAndEstado(idSede, estado, pageable).map(mapper::toDomain);
    }

    @Override public List<ReservaPublica> findConfirmadasBySedeAndFecha(Long idSede, LocalDate fecha) {
        return reservaJpa.findBySede_IdAndFechaEventoAndEstado(
                idSede, fecha, EstadoReservaPublica.CONFIRMADA).stream().map(mapper::toDomain).toList();
    }

    @Override public int countConfirmadasBySedeAndFecha(Long idSede, LocalDate fecha) {
        return reservaJpa.countConfirmadasBySedeAndFecha(idSede, fecha);
    }

    @Override
    public Page<ReservaPublica> buscarAdmin(
            Long idSede, EstadoReservaPublica estadoEnum, LocalDate fecha,
            Boolean ingresado, Boolean esReprogramacion, String searchPattern, Pageable pageable) {
        return reservaJpa.buscarAdmin(
                idSede, estadoEnum, fecha, ingresado, esReprogramacion, searchPattern, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public MetricasReservaQuery calcularMetricas(Long idSede, LocalDate fecha) {
        LocalDate dia = fecha != null ? fecha : LocalDate.now(ZoneId.of("America/Lima"));
        int total      = reservaJpa.countBySedeAndFechaAndEstado(idSede, dia, EstadoReservaPublica.PENDIENTE)
                       + reservaJpa.countBySedeAndFechaAndEstado(idSede, dia, EstadoReservaPublica.CONFIRMADA)
                       + reservaJpa.countBySedeAndFechaAndEstado(idSede, dia, EstadoReservaPublica.CANCELADA)
                       + reservaJpa.countBySedeAndFechaAndEstado(idSede, dia, EstadoReservaPublica.REPROGRAMADA)
                       + reservaJpa.countBySedeAndFechaAndEstado(idSede, dia, EstadoReservaPublica.COMPLETADA);
        int pendientes  = reservaJpa.countBySedeAndFechaAndEstado(idSede, dia, EstadoReservaPublica.PENDIENTE);
        int confirmadas = reservaJpa.countConfirmadasBySedeAndFecha(idSede, dia);
        int canceladas  = reservaJpa.countBySedeAndFechaAndEstado(idSede, dia, EstadoReservaPublica.CANCELADA);
        int ingresados  = reservaJpa.countIngresadosBySedeAndFecha(idSede, dia);

        return MetricasReservaQuery.builder()
                .fecha(dia)
                .totalReservas(total)
                .pendientes(pendientes)
                .confirmadas(confirmadas)
                .canceladas(canceladas)
                .ingresados(ingresados)
                .aforoMaximo(60)
                .aforoOcupado(confirmadas)
                .aforoRestante(Math.max(0, 60 - confirmadas))
                .ingresosDia(reservaJpa.sumIngresosBySedeAndFecha(idSede, dia))
                .build();
    }

    @Override
    @Transactional
    public ReservaPublica save(ReservaPublica reserva) {
        var cliente = clienteJpa.findById(reserva.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", reserva.getIdCliente()));
        var sede = sedeJpa.findById(reserva.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", reserva.getIdSede()));
        ReservaPublicaEntity original = reserva.getIdReservaOriginal() != null
                ? reservaJpa.findById(reserva.getIdReservaOriginal()).orElse(null) : null;
        return mapper.toDomain(reservaJpa.save(mapper.toEntity(reserva, cliente, sede, original)));
    }

    @Override public boolean existsByNumeroTicket(String ticket) {
        return reservaJpa.existsByNumeroTicket(ticket);
    }
}