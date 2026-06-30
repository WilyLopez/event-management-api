package com.playzone.pems.infrastructure.external.apisperu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApisPeruClient {

    private final RestTemplate restTemplate;

    public Map<String, Object> consultarDni(String dni, String urlBase, String token) {
        return get(urlBase, "/api/v1/dni/" + dni, token);
    }

    public Map<String, Object> consultarRuc(String ruc, String urlBase, String token) {
        return get(urlBase, "/api/v1/ruc/" + ruc, token);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> get(String urlBase, String path, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String cleanUrlBase = urlBase.endsWith("/") ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
        String url = cleanUrlBase + path + "?token=" + token;

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);
            log.info("APISPERU response [{}]: {}", path, response.getStatusCode());
            return response.getBody() != null ? response.getBody() : Map.of();
        } catch (Exception e) {
            log.error("Error al consultar APISPERU [{}]: {}", url, e.getMessage());
            throw new RuntimeException("Error en consulta de APISPERU: " + e.getMessage(), e);
        }
    }
}
