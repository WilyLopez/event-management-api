package com.playzone.pems.interfaces.rest.consulta;

import com.playzone.pems.shared.response.ApiResponse;
import com.playzone.pems.shared.util.TokenEncryptor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/sedes/{idSede}/integraciones")
@RequiredArgsConstructor
public class SedeIntegracionController {

    private final JdbcTemplate jdbcTemplate;
    private final TokenEncryptor tokenEncryptor;

    private static final String PLACEHOLDER = "••••••••••••••••";

    @GetMapping
    @PreAuthorize("hasAuthority('configuracion.editar')")
    public ResponseEntity<ApiResponse<List<SedeIntegracionResponse>>> listar(@PathVariable Long idSede) {
        List<SedeIntegracionResponse> response = new ArrayList<>();

        // Buscar configs guardados en la BD
        String sql = "SELECT proveedor_codigo, api_url, api_token_cifrado, limite_mensual, activo FROM public.sede_integracion WHERE idsede = ?";
        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(sql, idSede);
        } catch (Exception e) {
            log.warn("La tabla public.sede_integracion no existe o aun no ha sido creada: {}", e.getMessage());
            rows = List.of();
        }

        boolean tieneDecolecta = false;
        boolean tieneApisPeru = false;

        for (Map<String, Object> row : rows) {
            String prov = (String) row.get("proveedor_codigo");
            String url = (String) row.get("api_url");
            String tokenCifrado = (String) row.get("api_token_cifrado");
            int limite = ((Number) row.get("limite_mensual")).intValue();
            boolean activo = (Boolean) row.get("activo");

            boolean tieneToken = tokenCifrado != null && !tokenCifrado.isBlank();

            SedeIntegracionResponse config = SedeIntegracionResponse.builder()
                    .proveedorCodigo(prov)
                    .apiUrl(url)
                    .apiToken(tieneToken ? PLACEHOLDER : "")
                    .limiteMensual(limite)
                    .activo(activo)
                    .build();

            response.add(config);

            if ("DECOLECTA".equalsIgnoreCase(prov)) tieneDecolecta = true;
            if ("APISPERU".equalsIgnoreCase(prov)) tieneApisPeru = true;
        }

        // Agregar defaults si no existen en BD
        if (!tieneDecolecta) {
            response.add(SedeIntegracionResponse.builder()
                    .proveedorCodigo("DECOLECTA")
                    .apiUrl("https://api.decolecta.com")
                    .apiToken("")
                    .limiteMensual(100)
                    .activo(false)
                    .build());
        }
        if (!tieneApisPeru) {
            response.add(SedeIntegracionResponse.builder()
                    .proveedorCodigo("APISPERU")
                    .apiUrl("https://dniruc.apisperu.com")
                    .apiToken("")
                    .limiteMensual(2000)
                    .activo(false)
                    .build());
        }

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{proveedorCodigo}")
    @PreAuthorize("hasAuthority('configuracion.editar')")
    public ResponseEntity<ApiResponse<Void>> guardar(
            @PathVariable Long idSede,
            @PathVariable String proveedorCodigo,
            @RequestBody SedeIntegracionRequest request) {

        String prov = proveedorCodigo.toUpperCase();

        // 1. Si el proveedor que se va a guardar esta activo, desactivar todos los demas proveedores de la misma sede
        if (request.isActivo()) {
            String sqlDesactivar = "UPDATE public.sede_integracion SET activo = FALSE WHERE idsede = ? AND proveedor_codigo <> ?";
            try {
                jdbcTemplate.update(sqlDesactivar, idSede, prov);
            } catch (Exception e) {
                log.warn("Error al desactivar otros proveedores: {}", e.getMessage());
            }
        }

        // 2. Resolver token cifrado
        String tokenFinal = null;
        if (request.getApiToken() != null && !request.getApiToken().isBlank()) {
            if (PLACEHOLDER.equals(request.getApiToken())) {
                // Mantener el token actual de la BD
                String sqlGetToken = "SELECT api_token_cifrado FROM public.sede_integracion WHERE idsede = ? AND proveedor_codigo = ?";
                try {
                    tokenFinal = jdbcTemplate.queryForObject(sqlGetToken, String.class, idSede, prov);
                } catch (Exception e) {
                    tokenFinal = "";
                }
            } else {
                // Encriptar nuevo token
                tokenFinal = tokenEncryptor.encrypt(request.getApiToken());
            }
        }

        if (tokenFinal == null) {
            tokenFinal = "";
        }

        // 3. Insertar o actualizar la configuracion
        String sqlUpsert = "INSERT INTO public.sede_integracion (idsede, proveedor_codigo, api_url, api_token_cifrado, limite_mensual, activo, fecha_actualizacion) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW()) " +
                "ON CONFLICT (idsede, proveedor_codigo) " +
                "DO UPDATE SET api_url = EXCLUDED.api_url, api_token_cifrado = EXCLUDED.api_token_cifrado, " +
                "limite_mensual = EXCLUDED.limite_mensual, activo = EXCLUDED.activo, fecha_actualizacion = NOW()";

        try {
            jdbcTemplate.update(sqlUpsert, idSede, prov, request.getApiUrl(), tokenFinal, request.getLimiteMensual(), request.isActivo());
        } catch (Exception e) {
            log.error("Error al guardar integracion en public.sede_integracion: {}", e.getMessage());
            throw new RuntimeException("Error de base de datos al guardar la integracion. Asegurate de haber ejecutado el script SQL legacy.");
        }

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Getter
    @Builder
    public static class SedeIntegracionResponse {
        private String proveedorCodigo;
        private String apiUrl;
        private String apiToken;
        private int limiteMensual;
        private boolean activo;
    }

    @Getter
    public static class SedeIntegracionRequest {
        private String apiUrl;
        private String apiToken;
        private int limiteMensual;
        private boolean activo;
    }
}
