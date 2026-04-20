package com.playzone.pems.domain.fidelizacion.exception;

import com.playzone.pems.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class BeneficioNoAplicableException extends BusinessException {

    private static final String CODIGO = "BENEFICIO_NO_APLICABLE";

    public BeneficioNoAplicableException(String motivo) {
        super(
                "El beneficio de fidelización no puede aplicarse: " + motivo,
                HttpStatus.CONFLICT,
                CODIGO
        );
    }

    public BeneficioNoAplicableException(Long idCliente, int visitasActuales, int visitasRequeridas) {
        super(
                String.format(
                        "El cliente %d tiene %d visita(s) acumulada(s). Se requieren %d para obtener el beneficio.",
                        idCliente, visitasActuales, visitasRequeridas
                ),
                HttpStatus.CONFLICT,
                CODIGO
        );
    }
}