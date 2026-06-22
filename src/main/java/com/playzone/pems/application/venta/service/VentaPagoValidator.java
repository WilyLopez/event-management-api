package com.playzone.pems.application.venta.service;

import com.playzone.pems.application.venta.dto.command.PagoMostradorCommand;
import com.playzone.pems.shared.exception.ValidationException;
import java.math.BigDecimal;
import java.util.List;

public class VentaPagoValidator {

    public static BigDecimal validarYCalcularVuelto(
            List<PagoMostradorCommand> pagos,
            BigDecimal totalEsperado,
            BigDecimal efectivoRecibido) {
        BigDecimal sumaPagos = pagos.stream()
                .map(PagoMostradorCommand::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumaPagos.compareTo(totalEsperado) != 0) {
            throw new ValidationException(
                    "La suma de pagos (" + sumaPagos + ") no coincide con el total (" + totalEsperado + ").");
        }

        BigDecimal efectivoEnPagos = pagos.stream()
                .filter(p -> "EFECTIVO".equals(p.getMedioPago()))
                .map(PagoMostradorCommand::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal efectivoRecibidoFinal = efectivoRecibido != null ? efectivoRecibido : BigDecimal.ZERO;

        if (efectivoEnPagos.compareTo(BigDecimal.ZERO) > 0 && efectivoRecibidoFinal.compareTo(efectivoEnPagos) < 0) {
            throw new ValidationException("Efectivo recibido insuficiente.");
        }

        return efectivoRecibidoFinal.subtract(efectivoEnPagos).max(BigDecimal.ZERO);
    }
}
