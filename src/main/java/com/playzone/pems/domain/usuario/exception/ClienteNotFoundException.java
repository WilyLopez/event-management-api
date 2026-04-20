package com.playzone.pems.domain.usuario.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class ClienteNotFoundException extends ResourceNotFoundException {

    public ClienteNotFoundException(Long id) {
        super("Cliente", id);
    }

    public ClienteNotFoundException(String campo, Object valor) {
        super("Cliente", campo, valor);
    }
}