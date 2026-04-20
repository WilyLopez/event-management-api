package com.playzone.pems.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BusinessException {

    private static final String CODIGO = "UNAUTHORIZED";

    public UnauthorizedException() {
        super(
                "No autenticado. Por favor inicie sesión para continuar.",
                HttpStatus.UNAUTHORIZED,
                CODIGO
        );
    }

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, CODIGO);
    }
}