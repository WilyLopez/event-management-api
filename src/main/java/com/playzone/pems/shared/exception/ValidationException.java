package com.playzone.pems.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends BusinessException {

    private static final String CODIGO = "VALIDATION_ERROR";
    private final List<String> errores;

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, CODIGO);
        this.errores = List.of(message);
    }

    public ValidationException(List<String> errores) {
        super("Error de validación en los datos enviados.", HttpStatus.BAD_REQUEST, CODIGO);
        this.errores = List.copyOf(errores);
    }

    public ValidationException(String campo, String mensaje) {
        super(String.format("Campo '%s': %s", campo, mensaje), HttpStatus.BAD_REQUEST, CODIGO);
        this.errores = List.of(String.format("Campo '%s': %s", campo, mensaje));
    }
}