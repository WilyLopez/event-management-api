package com.playzone.pems.application.evento.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ConfirmarEventoCommand {
    private final Long               idEvento;
    private final BigDecimal         precioTotal;
    private final BigDecimal         montoAdelanto;
    private final UUID               idUsuarioGestor;

    /** Uno o más medios de pago para el adelanto. Puede ser vacío si montoAdelanto = 0. */
    private final List<VentaPagoItem> pagosAdelanto;

    /** AL_CONTADO | CUOTAS */
    private final String             modalidadPago;

    /** Solo obligatorio cuando modalidadPago = CUOTAS (mínimo 2). */
    private final Integer            numeroCuotas;

    /** Fecha tope para completar el pago total (solo CUOTAS). */
    private final LocalDate          fechaLimitePago;
}
