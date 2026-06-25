package com.playzone.pems.infrastructure.persistence.calendario.adapter;

import com.playzone.pems.domain.calendario.model.ProgramacionSemanal;
import com.playzone.pems.domain.calendario.repository.ProgramacionSemanalRepository;
import com.playzone.pems.infrastructure.persistence.calendario.entity.ProgramacionSemanalEntity;
import com.playzone.pems.infrastructure.persistence.calendario.jpa.ProgramacionSemanalJpaRepository;
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
public class ProgramacionSemanalPersistenceAdapter implements ProgramacionSemanalRepository {

    private final ProgramacionSemanalJpaRepository programacionJpa;
    private final SedeJpaRepository                sedeJpa;

    @Override
    @Transactional
    public ProgramacionSemanal guardar(ProgramacionSemanal programacion) {
        var sede = sedeJpa.findById(programacion.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", programacion.getIdSede()));

        ProgramacionSemanalEntity entity = ProgramacionSemanalEntity.builder()
                .id(programacion.getId())
                .sede(sede)
                .semanaInicio(programacion.getSemanaInicio())
                .semanaFin(programacion.getSemanaFin())
                .estado(programacion.getEstado() != null ? programacion.getEstado() : "ACTIVA")
                .autoGenerada(programacion.isAutoGenerada())
                .createdBy(programacion.getCreadoPor())
                .build();

        return toDomain(programacionJpa.save(entity));
    }

    @Override
    public Optional<ProgramacionSemanal> findById(Long id) {
        return programacionJpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<ProgramacionSemanal> findActivasBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return programacionJpa.findActivasBySedeAndRango(idSede, inicio, fin)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<ProgramacionSemanal> findActivasFuturasBySede(Long idSede) {
        return programacionJpa.findActivasFuturasBySede(idSede, LocalDate.now())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existeActivaEnFecha(Long idSede, LocalDate fecha) {
        return programacionJpa.existeActivaEnFecha(idSede, fecha);
    }

    @Override
    public boolean existeSolapamiento(Long idSede, LocalDate inicio, LocalDate fin) {
        return programacionJpa.existeSolapamiento(idSede, inicio, fin);
    }

    @Override
    public List<Long> findSedeIdsSinProgramacionEnSemana(LocalDate inicio, LocalDate fin) {
        return programacionJpa.findSedeIdsSinProgramacionEnSemana(inicio, fin);
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        programacionJpa.cancelar(id);
    }

    private ProgramacionSemanal toDomain(ProgramacionSemanalEntity e) {
        return ProgramacionSemanal.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .semanaInicio(e.getSemanaInicio())
                .semanaFin(e.getSemanaFin())
                .estado(e.getEstado())
                .autoGenerada(e.isAutoGenerada())
                .creadoPor(e.getCreatedBy())
                .creadoEn(e.getCreatedAt())
                .build();
    }
}
