package com.playzone.pems.interfaces.rest.facturacion.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ComprobanteResponse {
    private final Long          id;
    private final String        numeroCompleto;
    private final String        tipoComprobante;
    private final String        estadoComprobante;
    private final String        razonSocialReceptor;
    private final String        nroDocReceptor;
    private final BigDecimal    montoBase;
    private final BigDecimal    montoIgv;
    private final BigDecimal    montoTotal;
    private final String        pdfUrl;
    private final String        cdrEstado;
    private final LocalDateTime fechaEmision;
}