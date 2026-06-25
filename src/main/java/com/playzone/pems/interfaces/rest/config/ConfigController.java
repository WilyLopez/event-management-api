package com.playzone.pems.interfaces.rest.config;

import com.playzone.pems.shared.response.ApiResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    private static final List<MedioPagoResponse> MEDIOS_PAGO = List.of(
            new MedioPagoResponse("EFECTIVO",      "Efectivo"),
            new MedioPagoResponse("YAPE",          "Yape"),
            new MedioPagoResponse("TRANSFERENCIA", "Transferencia bancaria"),
            new MedioPagoResponse("TARJETA",       "Tarjeta")
    );

    @GetMapping("/medios-pago")
    public ResponseEntity<ApiResponse<List<MedioPagoResponse>>> getMediosPago() {
        return ResponseEntity.ok(ApiResponse.ok(MEDIOS_PAGO));
    }

    @Getter
    @Builder
    public static class MedioPagoResponse {
        private final String codigo;
        private final String label;

        public MedioPagoResponse(String codigo, String label) {
            this.codigo = codigo;
            this.label  = label;
        }
    }
}
