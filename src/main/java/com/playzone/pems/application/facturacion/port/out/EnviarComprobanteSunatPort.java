package com.playzone.pems.application.facturacion.port.out;

import com.playzone.pems.application.facturacion.dto.command.EmitirComprobanteCommand;

public interface EnviarComprobanteSunatPort {

    RespuestaSunat enviar(Long idComprobante, EmitirComprobanteCommand command);

    RespuestaSunat anular(Long idComprobante, String motivoAnulacion);

    record RespuestaSunat(
            boolean aceptado,
            String  cdrEstado,
            String  cdrDescripcion,
            String  hashSunat,
            String  xmlUrl,
            String  pdfUrl
    ) {}
}