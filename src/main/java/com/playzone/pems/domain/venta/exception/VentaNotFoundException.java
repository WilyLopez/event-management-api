package com.playzone.pems.domain.venta.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class VentaNotFoundException extends ResourceNotFoundException {

    public VentaNotFoundException(Long id) {
        super("Venta", id);
    }
}