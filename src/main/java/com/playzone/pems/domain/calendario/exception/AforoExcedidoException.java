package com.playzone.pems.domain.calendario.exception;

import com.playzone.pems.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class AforoExcedidoException extends BusinessException {

    private static final String CODIGO = "AFORO_EXCEDIDO";

    public AforoExcedidoException(LocalDate fecha, int aforoMaximo) {
        super(
                String.format(
                        "El aforo máximo (%d personas) para el %s ha sido alcanzado. " +
                                "No es posible confirmar más reservas para esa fecha.",
                        aforoMaximo, fecha
                ),
                HttpStatus.CONFLICT,
                CODIGO
        );
    }
}