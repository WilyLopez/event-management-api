package com.playzone.pems.application.calendario.service;

import com.playzone.pems.application.calendario.port.in.GestionarFeriadoUseCase;
import com.playzone.pems.domain.calendario.model.Feriado;
import com.playzone.pems.domain.calendario.repository.FeriadoRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import com.playzone.pems.shared.exception.ValidationException;
import com.playzone.pems.shared.util.FechaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeriadoService implements GestionarFeriadoUseCase {

    private final FeriadoRepository feriadoRepository;

    @Override
    @Transactional
    public Feriado crear(CrearCommand command) {
        if (command.fecha().isBefore(FechaUtil.hoy())) {
            throw new ValidationException("fecha", "No se pueden registrar feriados en fechas pasadas.");
        }
        if (command.descripcion() == null || command.descripcion().isBlank()) {
            throw new ValidationException("descripcion", "La descripcion del feriado es obligatoria.");
        }
        if (feriadoRepository.existsByFecha(command.fecha())) {
            throw new ValidationException("fecha", "Ya existe un feriado registrado en esta fecha.");
        }

        Feriado feriado = Feriado.builder()
                .tipoFeriado(command.tipo())
                .fecha(command.fecha())
                .descripcion(command.descripcion())
                .anio(command.fecha().getYear())
                .idUsuarioCreador(command.idUsuario())
                .build();

        return feriadoRepository.save(feriado);
    }

    @Override
    @Transactional
    public void eliminar(Long idFeriado) {
        feriadoRepository.findById(idFeriado)
                .orElseThrow(() -> new ResourceNotFoundException("Feriado", idFeriado));
        feriadoRepository.deleteById(idFeriado);
    }
}