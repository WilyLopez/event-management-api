package com.playzone.pems.interfaces.rest.comercial;

import com.playzone.pems.domain.comercial.repository.ServicioCotizacionRepository;
import com.playzone.pems.interfaces.rest.comercial.response.ServicioCotizacionResponse;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios-cotizacion")
@RequiredArgsConstructor
public class ServicioCotizacionController {

    private final ServicioCotizacionRepository servicioRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServicioCotizacionResponse>>> listar() {
        List<ServicioCotizacionResponse> response = servicioRepository.findAllActivos()
                .stream()
                .map(s -> ServicioCotizacionResponse.builder()
                        .id(s.getId())
                        .nombre(s.getNombre())
                        .descripcion(s.getDescripcion())
                        .precioReferencial(s.getPrecioReferencial())
                        .icono(s.getIcono())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
