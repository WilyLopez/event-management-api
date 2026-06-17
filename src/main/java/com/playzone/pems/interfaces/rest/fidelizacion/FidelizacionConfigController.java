package com.playzone.pems.interfaces.rest.fidelizacion;

import com.playzone.pems.domain.fidelizacion.model.FidelizacionConfig;
import com.playzone.pems.domain.fidelizacion.repository.FidelizacionConfigRepository;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fidelizacion/config")
@RequiredArgsConstructor
public class FidelizacionConfigController {

    private final FidelizacionConfigRepository configRepository;

    @GetMapping("/sedes/{idSede}")
    @PreAuthorize("hasAuthority('calendario.configurar')")
    public ResponseEntity<ApiResponse<FidelizacionConfig>> obtener(@PathVariable Long idSede) {
        FidelizacionConfig config = configRepository.findByIdSede(idSede)
                .orElse(FidelizacionConfig.builder().idSede(idSede).umbral(6).build());
        return ResponseEntity.ok(ApiResponse.ok(config));
    }

    @PutMapping("/sedes/{idSede}")
    @PreAuthorize("hasAuthority('calendario.configurar')")
    public ResponseEntity<ApiResponse<FidelizacionConfig>> actualizar(
            @PathVariable Long idSede,
            @RequestBody FidelizacionConfigRequest request) {
        
        FidelizacionConfig config = FidelizacionConfig.builder()
                .idSede(idSede)
                .umbral(request.umbral())
                .build();
        
        return ResponseEntity.ok(ApiResponse.ok(configRepository.save(config)));
    }

    public record FidelizacionConfigRequest(int umbral) {}
}
