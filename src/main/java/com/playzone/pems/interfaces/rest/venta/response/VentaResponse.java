package com.playzone.pems.interfaces.rest.venta.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class VentaResponse {

    private final Long          id;
    private final Long          idSede;
    private final Long          clienteId;
    private final Long          eventoId;
    private final String        tipo;
    private final String        canalCodigo;
    private final LocalDate     fechaVisita;
    private final BigDecimal    subtotal;
    private final BigDecimal    descuento;
    private final BigDecimal    total;
    private final String        notas;
    private final OffsetDateTime createdAt;
}
