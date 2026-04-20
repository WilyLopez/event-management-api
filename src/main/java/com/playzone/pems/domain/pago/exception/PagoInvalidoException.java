package com.playzone.pems.domain.pago.exception;

import com.playzone.pems.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PagoInvalidoException extends BusinessException {

    private static final String CODIGO = "PAGO_INVALIDO";

    public PagoInvalidoException(String motivo) {
        super(
                "El pago no puede procesarse: " + motivo,
                HttpStatus.UNPROCESSABLE_ENTITY,
                CODIGO
        );
    }
}