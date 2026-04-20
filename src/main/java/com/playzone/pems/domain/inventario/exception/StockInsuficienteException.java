package com.playzone.pems.domain.inventario.exception;

import com.playzone.pems.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class StockInsuficienteException extends BusinessException {

    private static final String CODIGO = "STOCK_INSUFICIENTE";

    public StockInsuficienteException(
            String nombreProducto,
            int stockActual,
            int cantidadSolicitada) {
        super(
                String.format(
                        "Stock insuficiente para '%s'. " +
                                "Disponible: %d unidad(es). Solicitado: %d unidad(es).",
                        nombreProducto, stockActual, cantidadSolicitada
                ),
                HttpStatus.CONFLICT,
                CODIGO
        );
    }
}