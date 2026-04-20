package com.playzone.pems.application.promocion.port.in;

import java.math.BigDecimal;

public interface AplicarPromocionUseCase {

    record Resultado(BigDecimal montoDescuento, Long idPromocion, String nombrePromocion) {}

    Resultado aplicar(Long idPromocion, Long idReservaPublica);
}