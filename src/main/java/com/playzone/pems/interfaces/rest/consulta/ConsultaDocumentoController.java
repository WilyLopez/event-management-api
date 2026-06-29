package com.playzone.pems.interfaces.rest.consulta;

import com.playzone.pems.infrastructure.external.apisperu.ApisPeruClient;
import com.playzone.pems.infrastructure.external.decolecta.DecolectaClient;
import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.util.TokenEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
public class ConsultaDocumentoController {

    private final DecolectaClient decolectaClient;
    private final ApisPeruClient apisPeruClient;
    private final TokenEncryptor tokenEncryptor;
    private final JdbcTemplate jdbcTemplate;

    @Value("${playzone.external.decolecta.url:https://api.decolecta.com}")
    private String defaultDecolectaUrl;

    @Value("${playzone.external.decolecta.token:changeme}")
    private String defaultDecolectaToken;

    @Value("${playzone.external.apisperu.url:https://dniruc.apisperu.com}")
    private String defaultApisPeruUrl;

    @Value("${playzone.external.apisperu.token:changeme}")
    private String defaultApisPeruToken;

    @GetMapping("/dni/{dni}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultarDni(
            @PathVariable String dni,
            @RequestParam Long idSede) {

        if (dni == null || dni.length() != 8 || !dni.matches("\\d+")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos numéricos");
        }

        Map<String, Object> result = ejecutarConsulta(idSede, "DNI", dni);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/ruc/{ruc}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultarRuc(
            @PathVariable String ruc,
            @RequestParam Long idSede) {

        if (ruc == null || ruc.length() != 11 || !ruc.matches("\\d+")) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos numéricos");
        }

        Map<String, Object> result = ejecutarConsulta(idSede, "RUC", ruc);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    private Map<String, Object> ejecutarConsulta(Long idSede, String tipoDoc, String numero) {
        String prov = "DECOLECTA";
        String url = defaultDecolectaUrl;
        String token = defaultDecolectaToken;
        int limite = 100;
        boolean usarConfigDb = false;

        // Intentar leer de public.sede_integracion
        try {
            String sql = "SELECT proveedor_codigo, api_url, api_token_cifrado, limite_mensual FROM public.sede_integracion WHERE idsede = ? AND activo = TRUE LIMIT 1";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idSede);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                prov = (String) row.get("proveedor_codigo");
                url = (String) row.get("api_url");
                String tokenCifrado = (String) row.get("api_token_cifrado");
                limite = ((Number) row.get("limite_mensual")).intValue();
                token = tokenEncryptor.decrypt(tokenCifrado);
                usarConfigDb = true;
            }
        } catch (Exception e) {
            log.warn("La tabla public.sede_integracion no existe o fallo consulta. Usando fallback de propiedades: {}", e.getMessage());
        }

        // Si no se usa configuración de BD, verificar si se configuró APIsPERU en el .env como fallback global
        if (!usarConfigDb) {
            if (defaultApisPeruToken != null && !defaultApisPeruToken.equals("changeme") && !defaultApisPeruToken.isBlank()) {
                prov = "APISPERU";
                url = defaultApisPeruUrl;
                token = defaultApisPeruToken;
                limite = 2000;
            }
        }

        if (usarConfigDb) {
            // Verificar limite mensual en registro_consulta_documento
            try {
                String sqlCount = "SELECT COUNT(*)::INT FROM public.registro_consulta_documento WHERE idsede = ? AND proveedor_codigo = ? AND creado_at >= DATE_TRUNC('month', CURRENT_DATE)";
                Integer count = jdbcTemplate.queryForObject(sqlCount, Integer.class, idSede, prov);
                if (count != null && count >= limite) {
                    throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "LIMIT_EXCEEDED");
                }
            } catch (ResponseStatusException rse) {
                throw rse;
            } catch (Exception e) {
                log.warn("La tabla public.registro_consulta_documento no existe o fallo conteo de limite mensual: {}", e.getMessage());
            }
        }

        Map<String, Object> responseData;

        // Ejecutar consulta según el proveedor
        if ("APISPERU".equalsIgnoreCase(prov)) {
            responseData = apisPeruClient.consultarDni(numero, url, token);
            if ("RUC".equals(tipoDoc)) {
                responseData = apisPeruClient.consultarRuc(numero, url, token);
            }
            // Mapeo unificado para que tenga los mismos campos
            if (responseData != null && responseData.containsKey("data")) {
                Object dataObj = responseData.get("data");
                if (dataObj instanceof Map) {
                    responseData = (Map<String, Object>) dataObj;
                }
            }
        } else {
            // Decolecta
            responseData = decolectaClient.consultarDni(numero, url, token);
            if ("RUC".equals(tipoDoc)) {
                responseData = decolectaClient.consultarRuc(numero, url, token);
            }
            if (responseData != null && responseData.containsKey("data")) {
                Object dataObj = responseData.get("data");
                if (dataObj instanceof Map) {
                    responseData = (Map<String, Object>) dataObj;
                }
            }
        }

        // Registrar consumo si se uso config de BD
        if (usarConfigDb) {
            try {
                String sqlInsert = "INSERT INTO public.registro_consulta_documento (idsede, proveedor_codigo, tipo_documento, numero_documento) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(sqlInsert, idSede, prov, tipoDoc, numero);
            } catch (Exception e) {
                log.warn("No se pudo registrar la auditoria de consulta: {}", e.getMessage());
            }
        }

        return responseData;
    }
}
