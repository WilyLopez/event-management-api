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

import com.playzone.pems.application.comercial.port.in.GestionarServiciosCotizacionUseCase;
import com.playzone.pems.domain.comercial.model.ServicioCotizacion;
import com.playzone.pems.interfaces.rest.comercial.response.ServicioCotizacionResponse;
import com.playzone.pems.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios-cotizacion")
@RequiredArgsConstructor
public class ServicioCotizacionController {

    private final GestionarServiciosCotizacionUseCase useCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServicioCotizacionResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarActivos().stream().map(this::toResponse).toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<List<ServicioCotizacionResponse>>> listarAdmin() {
        return ResponseEntity.ok(ApiResponse.ok(useCase.listarTodos().stream().map(this::toResponse).toList()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<ServicioCotizacionResponse>> crear(@RequestBody ServicioCotizacion s) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toResponse(useCase.crear(s))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<ServicioCotizacionResponse>> actualizar(@PathVariable Long id, @RequestBody ServicioCotizacion s) {
        ServicioCotizacion withId = s.toBuilder().id(id).build();
        return ResponseEntity.ok(ApiResponse.ok(toResponse(useCase.actualizar(withId))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('paquete.gestionar')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    private ServicioCotizacionResponse toResponse(ServicioCotizacion s) {
        return ServicioCotizacionResponse.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .precioReferencial(s.getPrecioReferencial())
                .icono(s.getIcono())
                .activo(s.isActivo())
                .orden(s.getOrden())
                .build();
    }
}
