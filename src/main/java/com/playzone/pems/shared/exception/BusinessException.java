package com.playzone.pems.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String codigoError;

    public BusinessException(String message) {
        super(message);
        this.status      = HttpStatus.BAD_REQUEST;
        this.codigoError = "BUSINESS_ERROR";
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status      = status;
        this.codigoError = "BUSINESS_ERROR";
    }

    public BusinessException(String message, HttpStatus status, String codigoError) {
        super(message);
        this.status      = status;
        this.codigoError = codigoError;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.status      = HttpStatus.BAD_REQUEST;
        this.codigoError = "BUSINESS_ERROR";
    }

    public BusinessException(String message, HttpStatus status, String codigoError, Throwable cause) {
        super(message, cause);
        this.status      = status;
        this.codigoError = codigoError;
    }
}