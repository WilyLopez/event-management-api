package com.playzone.pems.domain.inventario.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class ProductoNotFoundException extends ResourceNotFoundException {

    public ProductoNotFoundException(Long id) {
        super("Producto", id);
    }

    public ProductoNotFoundException(String campo, Object valor) {
        super("Producto", campo, valor);
    }
}