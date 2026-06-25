package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.dto.command.CrearProgramacionSemanalCommand;
import com.playzone.pems.application.calendario.dto.query.ProgramacionSemanalDto;
import com.playzone.pems.application.calendario.port.in.ProgramacionSemanalUseCase;
import com.playzone.pems.domain.calendario.model.ProgramacionSemanal;
import com.playzone.pems.domain.calendario.repository.ProgramacionSemanalRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramacionSemanalService implements ProgramacionSemanalUseCase {

    private final ProgramacionSemanalRepository programacionRepository;


    @Override
    @Transactional
    public ProgramacionSemanalDto crear(CrearProgramacionSemanalCommand command) {
        LocalDate hoy = FechaUtil.hoy();

        // V1 — la semana debe iniciar en lunes
        if (command.getSemanaInicio().getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new ValidationException("semanaInicio",
                    "La programacion semanal debe iniciar en lunes.");
        }

        if (!command.getSemanaFin().equals(command.getSemanaInicio().plusDays(6))) {
            throw new ValidationException("semanaFin",
                    "La programacion debe comprender exactamente 7 dias (lunes a domingo).");
        }

        LocalDate inicioSemanaActual = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        if (command.getSemanaInicio().isBefore(inicioSemanaActual)) {
            throw new ValidationException("semanaInicio",
                    "No se pueden programar semanas pasadas.");
        }

        if (programacionRepository.existeSolapamiento(
                command.getIdSede(), command.getSemanaInicio(), command.getSemanaFin())) {
            throw new ValidationException(
                    "Ya existe una programacion activa que se solapa con ese rango.");
        }

        return toDto(programacionRepository.guardar(buildDomain(command)));
    }

 
    @Override
    @Transactional
    public void cancelar(Long idSede, Long id) {
        ProgramacionSemanal prog = programacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramacionSemanal", id));

        if (!prog.getIdSede().equals(idSede)) {
            throw new ValidationException("La programacion no pertenece a la sede indicada.");
        }
        if (!prog.getSemanaInicio().isAfter(FechaUtil.hoy())) {
            throw new ValidationException(
                    "Solo se pueden cancelar semanas que aun no han iniciado.");
        }

        programacionRepository.cancelar(id);
        log.info("Programacion {} cancelada para sede {} (semana {})",
                id, idSede, prog.getSemanaInicio());
    }

   
    @Override
    @Transactional(readOnly = true)
    public List<ProgramacionSemanalDto> listarFuturas(Long idSede) {
        return programacionRepository.findActivasFuturasBySede(idSede)
                .stream().map(this::toDto).toList();
    }


    @Override
    @Transactional
    public void autoActivarSemanaActual() {
        LocalDate hoy         = FechaUtil.hoy();
        LocalDate semanaInicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate semanaFin    = semanaInicio.plusDays(6);

        List<Long> sedesSinCobertura =
                programacionRepository.findSedeIdsSinProgramacionEnSemana(semanaInicio, semanaFin);

        if (sedesSinCobertura.isEmpty()) {
            log.info("Auto-activacion semana {}: todas las sedes ya tienen programacion.", semanaInicio);
            return;
        }

        for (Long idSede : sedesSinCobertura) {
            try {
                ProgramacionSemanal nueva = ProgramacionSemanal.builder()
                        .idSede(idSede)
                        .semanaInicio(semanaInicio)
                        .semanaFin(semanaFin)
                        .estado("ACTIVA")
                        .autoGenerada(true)
                        .build();
                programacionRepository.guardar(nueva);
                log.info("Auto-activacion semana {}: programacion creada para sede {}.",
                        semanaInicio, idSede);
            } catch (Exception ex) {
                log.error("Auto-activacion semana {}: error en sede {} — {}",
                        semanaInicio, idSede, ex.getMessage());
            }
        }
    }


    private ProgramacionSemanal buildDomain(CrearProgramacionSemanalCommand c) {
        return ProgramacionSemanal.builder()
                .idSede(c.getIdSede())
                .semanaInicio(c.getSemanaInicio())
                .semanaFin(c.getSemanaFin())
                .estado("ACTIVA")
                .autoGenerada(c.isAutoGenerada())
                .creadoPor(c.getIdUsuarioAdmin())
                .build();
    }

    private ProgramacionSemanalDto toDto(ProgramacionSemanal p) {
        return ProgramacionSemanalDto.builder()
                .id(p.getId())
                .idSede(p.getIdSede())
                .semanaInicio(p.getSemanaInicio())
                .semanaFin(p.getSemanaFin())
                .estado(p.getEstado())
                .autoGenerada(p.isAutoGenerada())
                .creadoEn(p.getCreadoEn())
                .build();
    }
}
