package com.playzone.pems.infrastructure.external.sunat;

import com.playzone.pems.application.facturacion.dto.command.EmitirComprobanteCommand;
import com.playzone.pems.application.facturacion.port.out.EnviarComprobanteSunatPort.RespuestaSunat;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NubefactMapper {

    public Map<String, Object> toPayload(Long idComprobante, EmitirComprobanteCommand command) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("operacion", "generar_comprobante");
        payload.put("tipo_de_comprobante", command.getTipoComprobante().getCodigoSunat());
        payload.put("serie", extraerSerie(idComprobante));
        payload.put("numero", idComprobante.toString());
        payload.put("tipo_de_documento", command.getTipoDocReceptor().getCodigoSunat());
        payload.put("numero_de_documento", command.getNroDocReceptor());
        payload.put("denominacion", command.getRazonSocialReceptor());
        payload.put("direccion", command.getDireccionReceptor());
        return payload;
    }

    public RespuestaSunat toRespuesta(Map<String, Object> response) {
        boolean aceptado    = "0".equals(String.valueOf(response.getOrDefault("errors_count", "1")));
        String cdrEstado    = String.valueOf(response.getOrDefault("estado", "DESCONOCIDO"));
        String cdrDesc      = String.valueOf(response.getOrDefault("descripcion", "Sin descripción"));
        String hash         = String.valueOf(response.getOrDefault("hash", ""));
        String enlacePdf    = String.valueOf(response.getOrDefault("enlace_del_pdf", ""));
        String enlaceXml    = String.valueOf(response.getOrDefault("enlace_del_xml", ""));

        return new RespuestaSunat(aceptado, cdrEstado, cdrDesc, hash, enlaceXml, enlacePdf);
    }

    public RespuestaSunat toRespuestaAnulacion(Map<String, Object> response) {
        boolean aceptado = "0".equals(String.valueOf(response.getOrDefault("errors_count", "1")));
        String cdrEstado = String.valueOf(response.getOrDefault("estado", "DESCONOCIDO"));
        String cdrDesc   = String.valueOf(response.getOrDefault("descripcion", "Sin descripción"));
        return new RespuestaSunat(aceptado, cdrEstado, cdrDesc, null, null, null);
    }

    private String extraerSerie(Long idComprobante) {
        return "B001";
    }
}