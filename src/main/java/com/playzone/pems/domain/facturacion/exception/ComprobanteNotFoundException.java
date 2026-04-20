package com.playzone.pems.domain.facturacion.exception;

import com.playzone.pems.shared.exception.ResourceNotFoundException;

public class ComprobanteNotFoundException extends ResourceNotFoundException {

    public ComprobanteNotFoundException(Long id) {
        super("Comprobante", id);
    }

    public ComprobanteNotFoundException(String numeroCompleto) {
        super("Comprobante", "numeroCompleto", numeroCompleto);
    }
}