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
                String dbProv = (String) row.get("proveedor_codigo");
                String dbUrl = (String) row.get("api_url");
                String tokenCifrado = (String) row.get("api_token_cifrado");
                int dbLimite = ((Number) row.get("limite_mensual")).intValue();
                String dbToken = tokenEncryptor.decrypt(tokenCifrado);

                // Asignar solo si el desencriptado y consulta de datos es exitosa sin excepciones
                prov = dbProv;
                url = dbUrl;
                token = dbToken;
                limite = dbLimite;
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

        Map<String, Object> responseData = null;

        if ("APISPERU".equalsIgnoreCase(prov)) {
            Map<String, Object> rawResponse;
            if ("RUC".equals(tipoDoc)) {
                rawResponse = apisPeruClient.consultarRuc(numero, url, token);
            } else {
                rawResponse = apisPeruClient.consultarDni(numero, url, token);
            }
            if (rawResponse != null && rawResponse.containsKey("data")) {
                Object dataObj = rawResponse.get("data");
                if (dataObj instanceof Map) {
                    rawResponse = (Map<String, Object>) dataObj;
                }
            }
            responseData = unificarRespuesta(rawResponse, prov, tipoDoc);
        } else {
            if ("RUC".equals(tipoDoc)) {
                responseData = decolectaClient.consultarRuc(numero, url, token);
            } else {
                responseData = decolectaClient.consultarDni(numero, url, token);
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> unificarRespuesta(Map<String, Object> raw, String prov, String tipoDoc) {
        if (raw == null) return Map.of();
        
        java.util.HashMap<String, Object> unified = new java.util.HashMap<>();
        
        if ("APISPERU".equalsIgnoreCase(prov)) {
            if ("DNI".equalsIgnoreCase(tipoDoc)) {
                String docNum = raw.get("dni") != null ? (String) raw.get("dni") : (String) raw.get("numero");
                String name = (String) raw.get("nombres");
                String apPat = raw.get("apellidoPaterno") != null ? (String) raw.get("apellidoPaterno") : (String) raw.get("apellido_paterno");
                String apMat = raw.get("apellidoMaterno") != null ? (String) raw.get("apellidoMaterno") : (String) raw.get("apellido_materno");
                
                String fullName = null;
                if (raw.get("nombre_completo") != null) {
                    fullName = (String) raw.get("nombre_completo");
                } else if (raw.get("nombreCompleto") != null) {
                    fullName = (String) raw.get("nombreCompleto");
                } else {
                    fullName = ((apPat != null ? apPat : "") + " " + (apMat != null ? apMat : "") + " " + (name != null ? name : "")).trim();
                }

                unified.put("document_number", docNum);
                unified.put("first_name", name);
                unified.put("first_last_name", apPat);
                unified.put("second_last_name", apMat);
                unified.put("full_name", fullName);
            } else { // RUC
                String rucNum = raw.get("ruc") != null ? (String) raw.get("ruc") : 
                               (raw.get("numeroDocumento") != null ? (String) raw.get("numeroDocumento") : (String) raw.get("numero_documento"));
                String razSocial = raw.get("razonSocial") != null ? (String) raw.get("razonSocial") : (String) raw.get("razon_social");
                
                unified.put("numero_documento", rucNum);
                unified.put("razon_social", razSocial);
                unified.put("estado", raw.get("estado"));
                unified.put("condicion", raw.get("condicion"));
                unified.put("direccion", raw.get("direccion"));
                unified.put("via_tipo", raw.get("via_tipo"));
                unified.put("via_nombre", raw.get("via_nombre"));
                unified.put("numero", raw.get("numero"));
                unified.put("distrito", raw.get("distrito"));
                unified.put("provincia", raw.get("provincia"));
                unified.put("departamento", raw.get("departamento"));
            }
        } else {
            return raw;
        }
        
        return unified;
    }
}
