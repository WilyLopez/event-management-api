package com.playzone.pems.domain.promocion.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class PromocionNotFoundException extends ResourceNotFoundException {

    public PromocionNotFoundException(Long id) {
        super("Promocion", id);
    }

    public PromocionNotFoundException(String campo, Object valor) {
        super("Promocion", campo, valor);
    }
}