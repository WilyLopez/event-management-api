package com.playzone.pems.application.facturacion.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ComprobanteQuery {

    private final Long          id;
    private final String        numeroCompleto;
    private final String        tipoComprobante;
    private final String        estadoComprobante;
    private final String        rucEmisor;
    private final String        razonSocialEmisor;
    private final String        tipoDocReceptor;
    private final String        nroDocReceptor;
    private final String        razonSocialReceptor;
    private final BigDecimal    montoBase;
    private final BigDecimal    montoIgv;
    private final BigDecimal    montoTotal;
    private final String        pdfUrl;
    private final String        cdrEstado;
    private final String        cdrDescripcion;
    private final LocalDateTime fechaEmision;
}