package com.playzone.pems.domain.proveedor.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class ProveedorNotFoundException extends ResourceNotFoundException {

    public ProveedorNotFoundException(Long id) {
        super("Proveedor", id);
    }

    public ProveedorNotFoundException(String campo, Object valor) {
        super("Proveedor", campo, valor);
    }
}