package com.playzone.pems.infrastructure.external.sunat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NubefactClient {

    private final RestTemplate restTemplate;

    @Value("${playzone.sunat.nubefact.url}")
    private String urlBase;

    @Value("${playzone.sunat.nubefact.token}")
    private String token;

    @Value("${playzone.sunat.nubefact.ruc}")
    private String ruc;

    public Map<String, Object> emitirComprobante(Map<String, Object> payload) {
        return post("/api/v1/invoices", payload);
    }

    public Map<String, Object> anularComprobante(String serie, String correlativo, String motivo) {
        Map<String, Object> payload = Map.of(
                "serie", serie,
                "numero", correlativo,
                "motivo", motivo
        );
        return post("/api/v1/voids", payload);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String path, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(urlBase + path, request, Map.class);
            log.info("Nubefact response [{}]: {}", path, response.getStatusCode());
            return response.getBody() != null ? response.getBody() : Map.of();
        } catch (Exception e) {
            log.error("Error en llamada a Nubefact [{}]: {}", path, e.getMessage(), e);
            throw new RuntimeException("Error al comunicarse con Nubefact PSE: " + e.getMessage(), e);
        }
    }
}