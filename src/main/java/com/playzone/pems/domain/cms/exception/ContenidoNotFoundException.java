package com.playzone.pems.domain.cms.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class ContenidoNotFoundException extends ResourceNotFoundException {

    public ContenidoNotFoundException(Long id) {
        super("ContenidoWeb", id);
    }

    public ContenidoNotFoundException(String campo, Object valor) {
        super("ContenidoWeb", campo, valor);
    }
}