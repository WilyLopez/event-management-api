package com.playzone.pems.domain.facturacion.exception;

import com.playzone.pems.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SunatRechazadoException extends BusinessException {

    private static final String CODIGO = "SUNAT_RECHAZADO";

    public SunatRechazadoException(
            String numeroComprobante,
            String cdrEstado,
            String cdrDescripcion) {
        super(
                String.format(
                        "SUNAT rechazó el comprobante %s. Estado CDR: %s. Motivo: %s",
                        numeroComprobante, cdrEstado, cdrDescripcion
                ),
                HttpStatus.BAD_GATEWAY,
                CODIGO
        );
    }
}