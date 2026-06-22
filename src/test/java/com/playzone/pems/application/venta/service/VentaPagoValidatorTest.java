package com.playzone.pems.application.venta.service;

import com.playzone.pems.application.venta.dto.command.PagoMostradorCommand;
import com.playzone.pems.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VentaPagoValidatorTest {

    @Test
    void testValidarYCalcularVueltoExito() {
        PagoMostradorCommand pago1 = PagoMostradorCommand.builder()
                .medioPago("EFECTIVO")
                .monto(BigDecimal.valueOf(100.00))
                .build();
        PagoMostradorCommand pago2 = PagoMostradorCommand.builder()
                .medioPago("TARJETA")
                .monto(BigDecimal.valueOf(50.00))
                .build();

        BigDecimal vuelto = VentaPagoValidator.validarYCalcularVuelto(
                List.of(pago1, pago2),
                BigDecimal.valueOf(150.00),
                BigDecimal.valueOf(120.00)
        );

        assertEquals(BigDecimal.valueOf(20.00), vuelto);
    }

    @Test
    void testValidarYCalcularVueltoDiferenciaTotal() {
        PagoMostradorCommand pago = PagoMostradorCommand.builder()
                .medioPago("TARJETA")
                .monto(BigDecimal.valueOf(100.00))
                .build();

        assertThrows(ValidationException.class, () -> {
            VentaPagoValidator.validarYCalcularVuelto(
                    List.of(pago),
                    BigDecimal.valueOf(150.00),
                    BigDecimal.ZERO
            );
        });
    }

    @Test
    void testValidarYCalcularVueltoEfectivoInsuficiente() {
        PagoMostradorCommand pago = PagoMostradorCommand.builder()
                .medioPago("EFECTIVO")
                .monto(BigDecimal.valueOf(100.00))
                .build();

        assertThrows(ValidationException.class, () -> {
            VentaPagoValidator.validarYCalcularVuelto(
                    List.of(pago),
                    BigDecimal.valueOf(100.00),
                    BigDecimal.valueOf(50.00)
            );
        });
    }
}
