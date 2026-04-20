package com.playzone.pems.application.pago.port.out;

import java.math.BigDecimal;

public interface ProcesarPagoExternoPort {

    boolean verificarOperacion(String referenciaPago, BigDecimal montoEsperado);
}