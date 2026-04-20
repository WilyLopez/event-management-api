package com.playzone.pems.infrastructure.external.sunat;

import com.playzone.pems.application.facturacion.dto.command.EmitirComprobanteCommand;
import com.playzone.pems.application.facturacion.port.out.EnviarComprobanteSunatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SunatAdapter implements EnviarComprobanteSunatPort {

    private final NubefactClient  nubefactClient;
    private final NubefactMapper  nubefactMapper;

    @Override
    public RespuestaSunat enviar(Long idComprobante, EmitirComprobanteCommand command) {
        Map<String, Object> payload  = nubefactMapper.toPayload(idComprobante, command);
        Map<String, Object> response = nubefactClient.emitirComprobante(payload);
        return nubefactMapper.toRespuesta(response);
    }

    @Override
    public RespuestaSunat anular(Long idComprobante, String motivoAnulacion) {
        Map<String, Object> response = nubefactClient.anularComprobante(
                "B001", idComprobante.toString(), motivoAnulacion);
        return nubefactMapper.toRespuestaAnulacion(response);
    }
}