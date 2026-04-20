package com.playzone.pems.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BusinessException {

    private static final String CODIGO = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String recurso, String campo, Object valor) {
        super(
                String.format("%s no encontrado con %s: '%s'.", recurso, campo, valor),
                HttpStatus.NOT_FOUND,
                CODIGO
        );
    }

    public ResourceNotFoundException(String recurso, Long id) {
        super(
                String.format("%s no encontrado con id: %d.", recurso, id),
                HttpStatus.NOT_FOUND,
                CODIGO
        );
    }

    public ResourceNotFoundException(String mensaje) {
        super(mensaje, HttpStatus.NOT_FOUND, CODIGO);
    }
}