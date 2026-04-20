package com.playzone.pems.infrastructure.persistence.calendario.adapter;

import com.playzone.pems.domain.calendario.model.DisponibilidadDiaria;
import com.playzone.pems.domain.calendario.repository.DisponibilidadDiariaRepository;
import com.playzone.pems.infrastructure.persistence.calendario.entity.DisponibilidadDiariaEntity;
import com.playzone.pems.infrastructure.persistence.calendario.jpa.DisponibilidadDiariaJpaRepository;
import com.playzone.pems.infrastructure.persistence.calendario.mapper.CalendarioEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DisponibilidadDiariaPersistenceAdapter implements DisponibilidadDiariaRepository {

    private final DisponibilidadDiariaJpaRepository dispJpa;
    private final SedeJpaRepository                 sedeJpa;
    private final CalendarioEntityMapper            mapper;

    @Override
    public Optional<DisponibilidadDiaria> findBySedeAndFecha(Long idSede, LocalDate fecha) {
        return dispJpa.findBySede_IdAndFecha(idSede, fecha).map(mapper::toDomain);
    }

    @Override
    public List<DisponibilidadDiaria> findBySedeAndFechasBetween(Long idSede, LocalDate inicio, LocalDate fin) {
        return dispJpa.findBySede_IdAndFechaBetween(idSede, inicio, fin)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public DisponibilidadDiaria save(DisponibilidadDiaria d) {
        var sede = sedeJpa.findById(d.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", d.getIdSede()));

        DisponibilidadDiariaEntity entity = DisponibilidadDiariaEntity.builder()
                .id(d.getId())
                .sede(sede)
                .fecha(d.getFecha())
                .accesoPublicoActivo(d.isAccesoPublicoActivo())
                .turnoT1Disponible(d.isTurnoT1Disponible())
                .turnoT2Disponible(d.isTurnoT2Disponible())
                .aforoPublicoActual(d.getAforoPublicoActual())
                .build();

        return mapper.toDomain(dispJpa.save(entity));
    }

    @Override
    @Transactional
    public void incrementarAforo(Long idSede, LocalDate fecha) {
        dispJpa.incrementarAforo(idSede, fecha);
    }

    @Override
    @Transactional
    public void decrementarAforo(Long idSede, LocalDate fecha) {
        dispJpa.decrementarAforo(idSede, fecha);
    }

    @Override
    @Transactional
    public void bloquearAccesoPublico(Long idSede, LocalDate fecha) {
        dispJpa.bloquearAccesoPublico(idSede, fecha);
    }

    @Override
    @Transactional
    public void bloquearTurno(Long idSede, LocalDate fecha, String codigoTurno) {
        dispJpa.findBySede_IdAndFecha(idSede, fecha).ifPresent(d -> {
            if ("T1".equalsIgnoreCase(codigoTurno)) d.setTurnoT1Disponible(false);
            if ("T2".equalsIgnoreCase(codigoTurno)) d.setTurnoT2Disponible(false);
            dispJpa.save(d);
        });
    }
}