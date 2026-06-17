package com.playzone.pems.infrastructure.external.supabase;

import com.playzone.pems.application.usuario.port.out.SupabaseAuthPort;
import com.playzone.pems.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SupabaseAuthAdapter implements SupabaseAuthPort {

    private final RestTemplate restTemplate;

    @Value("${supabase.jwt.issuer}")
    private String authUrl;

    @Value("${supabase.auth.anon-key}")
    private String anonKey;

    @Override
    public java.util.Map<String, Object> login(String email, String password) {
        String url = authUrl + "/token?grant_type=password";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", anonKey);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error al iniciar sesión en Supabase: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public void actualizarPassword(String accessToken, String newPassword) {
        String url = authUrl + "/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", anonKey);
        headers.setBearerAuth(accessToken);

        Map<String, String> body = new HashMap<>();
        body.put("password", newPassword);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.put(url, request);
        } catch (HttpClientErrorException e) {
            log.error("Error al actualizar contraseña en Supabase: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al actualizar contraseña: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public void recuperarPassword(String email) {
        String url = authUrl + "/recover";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", anonKey);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Map.class);
        } catch (HttpClientErrorException e) {
            log.error("Error al solicitar recuperación en Supabase: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al solicitar recuperación: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public UUID crearUsuario(String email, String password, String nombreCompleto) {
        String url = authUrl + "/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", anonKey);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        Map<String, Object> data = new HashMap<>();
        data.put("full_name", nombreCompleto);
        body.put("user_metadata", data);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("id")) {
                return UUID.fromString((String) responseBody.get("id"));
            }
            throw new RuntimeException("Error al crear usuario en Supabase: respuesta invalida");
        } catch (HttpClientErrorException e) {
            log.error("Error al llamar a Supabase Auth: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getResponseBodyAsString().contains("User already registered")) {
                throw new ValidationException("correo", "El correo ya se encuentra registrado.");
            }
            throw new RuntimeException("Error al crear usuario en Supabase: " + e.getResponseBodyAsString());
        }
    }
}
