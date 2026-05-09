package com.playzone.pems.domain.contrato.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class ContratoNotFoundException extends ResourceNotFoundException {

    public ContratoNotFoundException(Long id) {
        super("Contrato", id);
    }

    public ContratoNotFoundException(String message) {
        super(message);
    }

    public ContratoNotFoundException(String campo, Object valor) {
        super("Contrato", campo, valor);
    }
}