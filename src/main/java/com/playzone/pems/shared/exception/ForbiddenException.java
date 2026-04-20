package com.playzone.pems.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends BusinessException {

    private static final String CODIGO = "FORBIDDEN";

    public ForbiddenException() {
        super(
                "No tiene permisos para realizar esta operación.",
                HttpStatus.FORBIDDEN,
                CODIGO
        );
    }

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, CODIGO);
    }
}