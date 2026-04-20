package com.playzone.pems.domain.calendario.exception;

import com.playzone.pems.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class FechaNoDisponibleException extends BusinessException {

    private static final String CODIGO = "FECHA_NO_DISPONIBLE";

    public FechaNoDisponibleException(LocalDate fecha) {
        super(
                String.format("La fecha %s no está disponible para reservas.", fecha),
                HttpStatus.CONFLICT,
                CODIGO
        );
    }

    public FechaNoDisponibleException(LocalDate fecha, String motivo) {
        super(
                String.format("La fecha %s no está disponible: %s", fecha, motivo),
                HttpStatus.CONFLICT,
                CODIGO
        );
    }
}