package com.playzone.pems.domain.calendario.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;

public class DisponibilidadNotFoundException extends ResourceNotFoundException {

    public DisponibilidadNotFoundException(Long idSede, LocalDate fecha) {
        super(String.format(
                "No se encontró disponibilidad para la sede %d en la fecha %s.",
                idSede, fecha
        ));
    }
}